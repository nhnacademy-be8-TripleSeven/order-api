package com.tripleseven.orderapi.business.order.process;

import com.tripleseven.orderapi.business.point.PointService;
import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.CombinedMessageDTO;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderSaveProcessing implements OrderProcessing {

    private static final String EXCHANGE_NAME = "nhn24.pay.exchange";

    private static final String CART_ROUTING_KEY = "cart.routing.key";
    private static final String POINT_ROUTING_KEY = "point.routing.key";

    public static final Long GUEST_USER_ID = -1L;


    private final OrderGroupService orderGroupService;
    private final OrderDetailService orderDetailService;
    private final DeliveryInfoService deliveryInfoService;
    private final PointService pointService;
    private final PayService payService;

    private final BookCouponApiClient bookCouponApiClient;

    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void processNonMemberOrder(String guestId, PaymentDTO paymentDTO) {
        // TODO Redis 저장 키 고민
        try {

            log.info("processNonMemberOrder guestId={}", guestId);

            HashOperations<String, String, PayInfoDTO> payHash = redisTemplate.opsForHash();

            if (Objects.isNull(payHash)) {
                throw new CustomException(ErrorCode.REDIS_NOT_FOUND);
            }

            PayInfoDTO payInfo = payHash.get(guestId, "PayInfo");

            OrderGroupCreateRequestDTO request = getOrderGroupCreateRequestDTO(payInfo);

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

            cartProcessing(guestId, bookInfos);

            log.info("Successfully processed non-member order");
        } catch (Exception e) {
            PayCancelRequestDTO payCancelRequest = new PayCancelRequestDTO("주문 오류");

            try {
                payService.cancelRequest(paymentDTO.getPaymentKey(), payCancelRequest);
            } catch (IOException ioe) {
                log.error(ioe.getMessage());
            }
        }
    }

    @Override
    public void processMemberOrder(Long memberId, PaymentDTO paymentDTO) {
        try {
            log.info("processMemberOrder memberId={}", memberId);

            HashOperations<String, String, PayInfoDTO> payHash = redisTemplate.opsForHash();

            if (Objects.isNull(payHash)) {
                throw new CustomException(ErrorCode.REDIS_NOT_FOUND);
            }

            PayInfoDTO payInfo = payHash.get(memberId.toString(), "PayInfo");

            OrderGroupCreateRequestDTO request = getOrderGroupCreateRequestDTO(payInfo);

            // OrderGroup 생성
            OrderGroupResponseDTO orderGroupResponseDTO = orderGroupService.createOrderGroup(memberId, request);
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

            Long couponId = payInfo.getCouponId();
            Long point = payInfo.getPoint();
            Long totalAmount = payInfo.getTotalAmount();

            // 쿠폰 사용
            if (couponId != null) {
                bookCouponApiClient.updateUseCoupon(couponId);
            }

            // 포인트 사용
            if (point > 0) {
                pointService.createPointHistoryForPaymentSpend(memberId, point, orderGroupId);
            }

            log.info("Successfully processed member order");
            // rabbitMQ 요청
            cartProcessing(memberId.toString(), bookInfos);
            pointProcessing(memberId, orderGroupId, totalAmount);

            log.info("Successfully processed RabbitMQ member order");

        } catch (Exception e) {
            PayCancelRequestDTO payCancelRequest = new PayCancelRequestDTO("주문 오류");

            try {
                payService.cancelRequest(paymentDTO.getPaymentKey(), payCancelRequest);
            } catch (IOException ioe) {
                log.error(ioe.getMessage());
            }
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

    // 장바구니 초기화
    private void cartProcessing(String userId, List<OrderBookInfoDTO> bookInfos) {
        List<Long> bookIds = bookInfos.stream().map(OrderBookInfoDTO::getBookId).toList();

        CombinedMessageDTO cartMessageDTO = new CombinedMessageDTO();
        cartMessageDTO.addObject("BookIds", bookIds);
        cartMessageDTO.addObject("UserId", userId);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, CART_ROUTING_KEY, cartMessageDTO);
    }

    // 포인트 적립
    private void pointProcessing(long userId, long orderId, long totalAmount) {
        CombinedMessageDTO pointMessageDTO = new CombinedMessageDTO();

        pointMessageDTO.addObject("UserId", userId);
        pointMessageDTO.addObject("TotalAmount", totalAmount);
        pointMessageDTO.addObject("OrderId", orderId);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, POINT_ROUTING_KEY, pointMessageDTO);
    }
}
