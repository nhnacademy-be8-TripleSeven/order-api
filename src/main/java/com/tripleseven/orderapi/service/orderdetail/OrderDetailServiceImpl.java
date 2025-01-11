package com.tripleseven.orderapi.service.orderdetail;

import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.dto.ordergrouppointhistory.OrderGroupPointHistoryRequestDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.exception.ReturnedFailedException;
import com.tripleseven.orderapi.exception.notfound.DeliveryInfoNotFoundException;
import com.tripleseven.orderapi.exception.notfound.OrderDetailNotFoundException;
import com.tripleseven.orderapi.exception.notfound.OrderGroupNotFoundException;
import com.tripleseven.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import com.tripleseven.orderapi.repository.orderdetail.OrderDetailRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.service.ordergrouppointhistory.OrderGroupPointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderGroupRepository orderGroupRepository;
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final OrderGroupPointHistoryService orderGroupPointHistoryService;
    private final BookCouponApiClient bookCouponApiClient;

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDTO getOrderDetailService(Long id) {
        Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(id);
        if (optionalOrderDetail.isEmpty()) {
            throw new OrderDetailNotFoundException(id);
        }

        return OrderDetailResponseDTO.fromEntity(optionalOrderDetail.get());
    }

    @Override
    @Transactional
    public OrderDetailResponseDTO createOrderDetail(OrderDetailCreateRequestDTO orderDetailCreateRequestDTO) {
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(orderDetailCreateRequestDTO.getOrderGroupId());

        if (optionalOrderGroup.isEmpty()) {
            throw new OrderGroupNotFoundException(orderDetailCreateRequestDTO.getOrderGroupId());
        }

        OrderGroup orderGroup = optionalOrderGroup.get();

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.ofCreate(
                orderDetailCreateRequestDTO.getBookId(),
                orderDetailCreateRequestDTO.getAmount(),
                orderDetailCreateRequestDTO.getPrimePrice(),
                orderDetailCreateRequestDTO.getDiscountPrice(),
                orderGroup);

        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);

        return OrderDetailResponseDTO.fromEntity(savedOrderDetail);
    }

    // 주문 상태 업데이트(회원)
    @Override
    @Transactional
    public List<OrderDetailResponseDTO> updateOrderDetailStatus(List<Long> ids, OrderStatus orderStatus) {
        if (ids.isEmpty()) {
            throw new RuntimeException();
        }
        List<OrderDetailResponseDTO> orderDetailResponses = new ArrayList<>();

        if (orderStatus.equals(OrderStatus.ORDER_CANCELED)) {

            for (Long id : ids) {
                Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(id);

                if (optionalOrderDetail.isEmpty()) {
                    throw new OrderDetailNotFoundException(id);
                }

                OrderDetail orderDetail = optionalOrderDetail.get();

                // 결제 대기 중이나 결제 완료 일때만 취소 가능
                if (!orderDetail.getOrderStatus().equals(OrderStatus.PAYMENT_PENDING) && !orderDetail.getOrderStatus().equals(OrderStatus.PAYMENT_COMPLETED)) {
                    throw new RuntimeException();
                }
                orderDetail.ofUpdateStatus(orderStatus);

                orderDetailResponses.add(OrderDetailResponseDTO.fromEntity(orderDetail));
            }
        } else {
            for (Long id : ids) {
                Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(id);

                if (optionalOrderDetail.isEmpty()) {
                    throw new OrderDetailNotFoundException(id);
                }

                OrderDetail orderDetail = optionalOrderDetail.get();

                if (!orderDetail.getOrderStatus().equals(OrderStatus.SHIPPING) || !orderDetail.getOrderStatus().equals(OrderStatus.DELIVERED)) {
                    throw new RuntimeException();
                }

                // 배송 완료 될 시에만 로직 실행
                if (orderDetail.getOrderStatus().equals(OrderStatus.DELIVERED)) {
                    Long orderGroupId = orderDetail.getOrderGroup().getId();

                    Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(orderGroupId);
                    if (optionalOrderGroup.isEmpty()) {
                        throw new OrderGroupNotFoundException(orderGroupId);
                    }

                    Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(orderGroupId);

                    if (optionalDeliveryInfo.isEmpty()) {
                        throw new DeliveryInfoNotFoundException(id);
                    }

                    DeliveryInfo deliveryInfo = optionalDeliveryInfo.get();
                    LocalDate today = LocalDate.now();

                    // 출고일 기준
                    if (today.isAfter(deliveryInfo.getShippingAt().plusMonths(30))) {
                        throw new ReturnedFailedException("over day :" + deliveryInfo.getShippingAt());
                    }
                    orderDetail.ofUpdateStatus(orderStatus);
                }

                orderDetailResponses.add(OrderDetailResponseDTO.fromEntity(orderDetail));
            }
        }
        return orderDetailResponses;
    }

    @Override
    @Transactional
    public List<OrderDetailResponseDTO> updateAdminOrderDetailStatus(List<Long> ids, OrderStatus orderStatus) {
        List<OrderDetailResponseDTO> orderDetailResponses = new ArrayList<>();
        for (Long id : ids) {
            Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(id);

            if (optionalOrderDetail.isEmpty()) {
                throw new OrderDetailNotFoundException(id);
            }

            OrderDetail orderDetail = optionalOrderDetail.get();

            // 반품 시 환불 로직
            if (orderStatus.equals(OrderStatus.RETURNED)) {
                Long orderGroupId = orderDetail.getOrderGroup().getId();
                Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(orderGroupId);
                if (optionalOrderDetail.isEmpty()) {
                    throw new OrderGroupNotFoundException(orderGroupId);
                }
                OrderGroup orderGroup = optionalOrderGroup.get();
                Long userId = orderGroup.getUserId();
                // 환불 금액 (쿠폰은 재발급 x)
                int refundPrice = orderDetail.getPrimePrice() - orderDetail.getDiscountPrice();

                String bookName = bookCouponApiClient.getBookName(orderDetail.getBookId());

                // 배송비 감면
                Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(orderGroupId);

                if (optionalDeliveryInfo.isEmpty()) {
                    throw new DeliveryInfoNotFoundException(orderGroupId);
                }

                DeliveryInfo deliveryInfo = optionalDeliveryInfo.get();
                LocalDate today = LocalDate.now();

                // 관리자는 무조건 바꿀 수 있도록
                LocalDate shippingAt = Objects.nonNull(deliveryInfo.getShippingAt()) ? deliveryInfo.getShippingAt() : today;

                // 출고일 기준 (10일 이후이면 배송비 환불 불가)
                if (today.isAfter(shippingAt.plusDays(10))) {
                    refundPrice -= orderGroup.getDeliveryPrice();
                }
                PointHistory pointHistory = PointHistory.ofCreate(
                        HistoryTypes.EARN,
                        refundPrice,
                        String.format("환불: " + bookName),
                        userId
                );
                PointHistory savedPointHistory = pointHistoryRepository.save(pointHistory);


                orderGroupPointHistoryService.createOrderGroupPointHistory(
                        new OrderGroupPointHistoryRequestDTO(
                                orderGroupId,
                                savedPointHistory.getId()
                        ));
            }
            orderDetail.ofUpdateStatus(orderStatus);

            orderDetailResponses.add(OrderDetailResponseDTO.fromEntity(orderDetail));
        }

        return orderDetailResponses;
    }

    @Override
    @Transactional
    public void deleteOrderDetail(Long id) {
        if (!orderDetailRepository.existsById(id)) {
            throw new OrderDetailNotFoundException(id);
        }
        orderDetailRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetailResponseDTO> getOrderDetailsToList(Long orderGroupId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderGroupId(orderGroupId);

        if (orderDetails.isEmpty()) {
            return List.of();
        }

        return orderDetails.stream().map(OrderDetailResponseDTO::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserPurchasedBook(Long userId, Long bookId) {
        // OrderGroup과 OrderDetail을 조인하여 해당 유저가 특정 도서를 구매했는지 확인
        return orderDetailRepository.existsByOrderGroupUserIdAndBookId(userId, bookId);
    }
}
