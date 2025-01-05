package com.tripleseven.orderapi.service.orderdetail;

import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.exception.notfound.DeliveryInfoNotFoundException;
import com.tripleseven.orderapi.exception.notfound.OrderDetailNotFoundException;
import com.tripleseven.orderapi.exception.notfound.OrderGroupNotFoundException;
import com.tripleseven.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import com.tripleseven.orderapi.repository.orderdetail.OrderDetailRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderGroupRepository orderGroupRepository;
    private final DeliveryInfoRepository deliveryInfoRepository;

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

        List<OrderDetailResponseDTO> orderDetailResponses = new ArrayList<>();

        if (orderStatus.equals(OrderStatus.ORDER_CANCELED)) {

            for (Long id : ids) {
                Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(id);

                if (optionalOrderDetail.isEmpty()) {
                    throw new OrderDetailNotFoundException(id);
                }

                OrderDetail orderDetail = optionalOrderDetail.get();

                if (orderDetail.getOrderStatus().equals(OrderStatus.PAYMENT_PENDING) || orderDetail.getOrderStatus().equals(OrderStatus.PAYMENT_COMPLETED)) {
                    orderDetail.ofUpdateStatus(orderStatus);
                }

                orderDetailResponses.add(OrderDetailResponseDTO.fromEntity(orderDetail));
            }
        } else {
            for (Long id : ids) {
                Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(id);

                if (optionalOrderDetail.isEmpty()) {
                    throw new OrderDetailNotFoundException(id);
                }

                OrderDetail orderDetail = optionalOrderDetail.get();

                if (orderDetail.getOrderStatus().equals(OrderStatus.DELIVERED)) {
                    Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(id);

                    if (optionalDeliveryInfo.isEmpty()) {
                        throw new DeliveryInfoNotFoundException(id);
                    }

                    DeliveryInfo deliveryInfo = optionalDeliveryInfo.get();
                    LocalDate today = LocalDate.now();

                    // 출고일 기준
                    if (deliveryInfo.getShippingAt().isBefore(today.minusDays(30L))) {
                        orderDetail.ofUpdateStatus(orderStatus);
                    }
                }

                orderDetailResponses.add(OrderDetailResponseDTO.fromEntity(orderDetail));
            }
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
    public List<OrderDetailResponseDTO> getOrderDetailsForGroupWithStatus(Long orderGroupId, OrderStatus orderStatus) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserPurchasedBook(Long userId, Long bookId) {
        // OrderGroup과 OrderDetail을 조인하여 해당 유저가 특정 도서를 구매했는지 확인
        return orderDetailRepository.existsByOrderGroupUserIdAndBookId(userId, bookId);
    }
}
