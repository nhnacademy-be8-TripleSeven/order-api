package com.tripleseven.orderapi.business.order;

import com.tripleseven.orderapi.business.feign.BookService;
import com.tripleseven.orderapi.business.point.PointService;
import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequestDTO;
import com.tripleseven.orderapi.dto.order.*;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.pay.PayCancelRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PaymentDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponseDTO;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.service.deliveryinfo.DeliveryInfoService;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.ordergrouppointhistory.OrderGroupPointHistoryService;
import com.tripleseven.orderapi.service.pay.PayService;
import com.tripleseven.orderapi.service.wrapping.WrappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderDetailService orderDetailService;
    private final OrderGroupService orderGroupService;
    private final WrappingService wrappingService;
    private final OrderGroupPointHistoryService orderGroupPointHistoryService;
    private final DeliveryInfoService deliveryInfoService;
    private final BookCouponApiClient bookCouponApiClient;
    private final PayService payService;

    private final BookService bookService;
    private final PointService pointService;

    @Override
    @Transactional(readOnly = true)
    public OrderPayDetailDTO getOrderPayDetail(Long userId, Long orderGroupId) {
        OrderGroupResponseDTO response = orderGroupService.getOrderGroupById(orderGroupId);

        if (!response.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return new OrderPayDetailDTO(
                this.getOrderInfos(orderGroupId),
                this.getOrderGroupInfo(orderGroupId),
                this.getDeliveryInfo(orderGroupId),
                this.getOrderPayInfo(orderGroupId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderPayDetailDTO getOrderPayDetailAdmin(Long orderGroupId) {
        return new OrderPayDetailDTO(
                this.getOrderInfos(orderGroupId),
                this.getOrderGroupInfo(orderGroupId),
                this.getDeliveryInfo(orderGroupId),
                this.getOrderPayInfo(orderGroupId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Long getThreeMonthsNetAmount(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate threeMonthsAgo = today.minusMonths(3);
        return orderDetailService.getNetTotalByPeriod(userId, threeMonthsAgo, today);
    }

    // 미리 flush 처리
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long saveOrderInfo(Long userId, PayInfoDTO payInfo, PaymentDTO payment, OrderGroupCreateRequestDTO request) {
        try {
            // OrderGroup 생성
            OrderGroupResponseDTO orderGroupResponseDTO = orderGroupService.createOrderGroup(userId, request);
            Long orderGroupId = orderGroupResponseDTO.getId();

            // DeliveryInfo 생성
            deliveryInfoService.createDeliveryInfo(
                    new DeliveryInfoCreateRequestDTO(orderGroupId, payInfo.getDeliveryDate())
            );
            List<OrderBookInfoDTO> bookInfos = payInfo.getBookOrderDetails();

            // OrderDetail 저장
            for (OrderBookInfoDTO bookInfo : bookInfos) {
                OrderDetailCreateRequestDTO orderDetailCreateRequestDTO =
                        new OrderDetailCreateRequestDTO(
                                bookInfo.getBookId(),
                                bookInfo.getQuantity(),
                                bookInfo.getPrice(),
                                bookInfo.getCouponSalePrice(),
                                orderGroupId
                        );
                orderDetailService.createOrderDetail(orderDetailCreateRequestDTO);

                // 쿠폰 사용
                if (bookInfo.getCouponId() != null) {
                    bookService.useCoupon(bookInfo.getCouponId());
                }

            }

            payService.createPay(payment, orderGroupId, payInfo.getPayType());

            // 포인트 사용
            if (payInfo.getPoint() > 0) {
                pointService.createPointHistoryForPaymentSpend(userId, payInfo.getPoint(), orderGroupId);
            }

            log.info("Successfully processed member order");

            return orderGroupId;
        } catch (Exception e) {
            handlePaymentCancellation(payment, "주문 저장 오류");
            throw e;
        }
    }


    private List<OrderInfoDTO> getOrderInfos(Long orderGroupId) {
        List<OrderDetailResponseDTO> orderDetailResponseList = orderDetailService.getOrderDetailsToList(orderGroupId);

        List<OrderInfoDTO> orderInfoList = new ArrayList<>();
        for (OrderDetailResponseDTO orderDetailResponseDTO : orderDetailResponseList) {

            String bookName = bookCouponApiClient.getBookName(orderDetailResponseDTO.getBookId());

            OrderInfoDTO orderInfoDTO = new OrderInfoDTO(
                    orderDetailResponseDTO.getId(),
                    orderDetailResponseDTO.getOrderStatus(),
                    bookName,
                    orderDetailResponseDTO.getQuantity(),
                    orderDetailResponseDTO.getDiscountPrice(),
                    orderDetailResponseDTO.getPrimePrice());

            orderInfoList.add(orderInfoDTO);
        }
        return orderInfoList;
    }

    private OrderGroupInfoDTO getOrderGroupInfo(Long orderGroupId) {
        OrderGroupResponseDTO orderGroupResponseDTO = orderGroupService.getOrderGroupById(orderGroupId);
        List<OrderDetailResponseDTO> orderDetailResponseList = orderDetailService.getOrderDetailsToList(orderGroupId);

        long usedPoint = orderGroupPointHistoryService.getUsedPoint(orderGroupId);
        long earnedPoint = orderGroupPointHistoryService.getEarnedPoint(orderGroupId);
        // 판매가 총합
        long primeTotalPrice = 0;
        // 할인 금액
        long discountPrice = 0;

        for (OrderDetailResponseDTO orderDetailResponseDTO : orderDetailResponseList) {
            primeTotalPrice += orderDetailResponseDTO.getPrimePrice() * orderDetailResponseDTO.getQuantity();
            discountPrice += orderDetailResponseDTO.getDiscountPrice();
        }

        long totalPrice;
        Long wrappingId = orderGroupResponseDTO.getWrappingId();
        if (Objects.isNull(wrappingId)) {
            totalPrice = primeTotalPrice - discountPrice + orderGroupResponseDTO.getDeliveryPrice();
            return new OrderGroupInfoDTO(
                    primeTotalPrice,
                    discountPrice,
                    orderGroupResponseDTO.getDeliveryPrice(),
                    "포장안함",
                    0,
                    totalPrice,
                    usedPoint,
                    earnedPoint);
        }
        WrappingResponseDTO wrappingResponseDTO = wrappingService.getWrappingById(orderGroupResponseDTO.getWrappingId());
        // 총 계산된 금액
        totalPrice = primeTotalPrice - discountPrice + wrappingResponseDTO.getPrice() + orderGroupResponseDTO.getDeliveryPrice();

        return new OrderGroupInfoDTO(
                primeTotalPrice,
                discountPrice,
                orderGroupResponseDTO.getDeliveryPrice(),
                wrappingResponseDTO.getName(),
                wrappingResponseDTO.getPrice(),
                totalPrice,
                usedPoint,
                earnedPoint);
    }

    private DeliveryInfoDTO getDeliveryInfo(Long orderGroupId) {
        return deliveryInfoService.getDeliveryInfoDTO(orderGroupId);
    }

    private OrderPayInfoDTO getOrderPayInfo(Long orderGroupId) {
        return payService.getOrderPayInfo(orderGroupId);
    }

    // 결제 취소 처리
    private void handlePaymentCancellation(PaymentDTO paymentDTO, String reason) {
        try {
            PayCancelRequestDTO payCancelRequest = new PayCancelRequestDTO(reason);
            payService.cancelRequest(paymentDTO.getPaymentKey(), payCancelRequest);
            log.info("Payment cancellation request completed.");
            throw new CustomException(ErrorCode.BAD_REQUEST);
        } catch (IOException ioe) {
            log.error("Failed to process payment cancellation: {}", ioe.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
