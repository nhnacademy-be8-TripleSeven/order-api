package com.tripleseven.orderapi.business.order.process;

import com.tripleseven.orderapi.business.feign.BookService;
import com.tripleseven.orderapi.business.point.PointService;
import com.tripleseven.orderapi.business.rabbit.RabbitService;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequestDTO;
import com.tripleseven.orderapi.dto.order.AddressInfoDTO;
import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import com.tripleseven.orderapi.dto.order.RecipientInfoDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.pay.PayCancelRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PaymentDTO;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.service.deliveryinfo.DeliveryInfoService;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.pay.PayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSaveProcessing implements OrderProcessing {
    public static final Long GUEST_USER_ID = -1L;

    private final OrderGroupService orderGroupService;
    private final OrderDetailService orderDetailService;
    private final DeliveryInfoService deliveryInfoService;
    private final PointService pointService;
    private final PayService payService;
    private final BookService bookService;
    private final RabbitService rabbitService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public void processNonMemberOrder(String guestId, PaymentDTO paymentDTO) {
        // TODO Redis 저장 키 고민
        log.info("processNonMemberOrder guestId={}", guestId);

        HashOperations<String, String, PayInfoDTO> payHash = redisTemplate.opsForHash();

        if (Objects.isNull(payHash)) {
            throw new CustomException(ErrorCode.REDIS_NOT_FOUND);
        }

        PayInfoDTO payInfo = payHash.get(guestId, "PayInfo");

        OrderGroupCreateRequestDTO request = getOrderGroupCreateRequestDTO(payInfo);
        try {
            // OrderGroup 생성
            OrderGroupResponseDTO orderGroupResponseDTO = orderGroupService.createOrderGroup(GUEST_USER_ID, request);
            Long orderGroupId = orderGroupResponseDTO.getId();

            // DeliveryInfo 생성
            deliveryInfoService.createDeliveryInfo(
                    new DeliveryInfoCreateRequestDTO(orderGroupId, payInfo.getDeliveryDate())
            );

            // OrderDetail 저장
            List<OrderBookInfoDTO> bookInfos = payInfo.getBookOrderDetails();
            for (OrderBookInfoDTO bookInfo : bookInfos) {
                OrderDetailCreateRequestDTO orderDetailCreateRequestDTO
                        = new OrderDetailCreateRequestDTO(
                        bookInfo.getBookId(),
                        bookInfo.getQuantity(),
                        bookInfo.getPrice(),
                        bookInfo.getCouponSalePrice(),
                        orderGroupId
                );
                orderDetailService.createOrderDetail(orderDetailCreateRequestDTO);
            }

            payService.createPay(paymentDTO, orderGroupId);

            rabbitService.sendCartMessage(guestId, bookInfos);

            log.info("Successfully processed non-member order");
        } catch (Exception e) {
            handlePaymentCancellation(paymentDTO, "주문 오류");
            throw e;
        }

    }

    @Override
    @Transactional
    public void processMemberOrder(Long memberId, PaymentDTO paymentDTO) {
        log.info("processMemberOrder memberId={}", memberId);

        HashOperations<String, String, PayInfoDTO> payHash = redisTemplate.opsForHash();

        if (Objects.isNull(payHash)) {
            throw new CustomException(ErrorCode.REDIS_NOT_FOUND);
        }

        PayInfoDTO payInfo = payHash.get(memberId.toString(), "PayInfo");
        OrderGroupCreateRequestDTO request = getOrderGroupCreateRequestDTO(payInfo);

        Long orderGroupId = null;

        try {
            // OrderGroup 생성
            OrderGroupResponseDTO orderGroupResponseDTO = orderGroupService.createOrderGroup(memberId, request);
            orderGroupId = orderGroupResponseDTO.getId();

            // DeliveryInfo 생성
            deliveryInfoService.createDeliveryInfo(
                    new DeliveryInfoCreateRequestDTO(orderGroupId, payInfo.getDeliveryDate())
            );

            // OrderDetail 저장
            List<OrderBookInfoDTO> bookInfos = payInfo.getBookOrderDetails();
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
            }

            // 결제 저장
            payService.createPay(paymentDTO, orderGroupId);

            // 쿠폰 사용
            if (payInfo.getCouponId() != null) {
                bookService.useCoupon(payInfo.getCouponId());
            }

            // 포인트 사용
            if (payInfo.getPoint() > 0) {
                pointService.createPointHistoryForPaymentSpend(memberId, payInfo.getPoint(), orderGroupId);
            }

            log.info("Successfully processed member order");

            // RabbitMQ 처리
            rabbitService.sendCartMessage(memberId.toString(), bookInfos);
            rabbitService.sendPointMessage(memberId, orderGroupId, payInfo.getTotalAmount());

        } catch (Exception e) {
            handlePaymentCancellation(paymentDTO, "주문 오류");
            throw e; // 예외를 다시 던져 롤백 트리거
        }
    }

    // 결제 취소 처리
    private void handlePaymentCancellation(PaymentDTO paymentDTO, String reason) {
        try {
            PayCancelRequestDTO payCancelRequest = new PayCancelRequestDTO(reason);
            payService.cancelRequest(paymentDTO.getPaymentKey(), payCancelRequest);
            log.info("Payment cancellation request completed.");
        } catch (IOException ioe) {
            log.error("Failed to process payment cancellation: {}", ioe.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private OrderGroupCreateRequestDTO getOrderGroupCreateRequestDTO(PayInfoDTO payInfo) {
        AddressInfoDTO addressInfo = payInfo.getAddressInfo();
        RecipientInfoDTO recipientInfo = payInfo.getRecipientInfo();

        String address = String.format("%s %s (%s)",
                addressInfo.getRoadAddress().trim(),
                addressInfo.getDetailAddress().trim(),
                addressInfo.getZoneAddress()).trim();

        return new OrderGroupCreateRequestDTO(
                payInfo.getWrapperId(),
                payInfo.getOrdererName(),
                recipientInfo.getRecipientName(),
                recipientInfo.getRecipientPhone(),
                recipientInfo.getRecipientLandline(),
                payInfo.getDeliveryFee(),
                address
        );
    }
}
