package com.tripleseven.orderapi.common.rabbit;

import com.rabbitmq.client.Channel;
import com.tripleseven.orderapi.business.pointservice.PointService;
import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.client.MemberApiClient;
import com.tripleseven.orderapi.dto.CombinedMessageDTO;
import com.tripleseven.orderapi.dto.cartitem.CartItemDTO;
import com.tripleseven.orderapi.dto.cartitem.CartUpdateRequestDTO;
import com.tripleseven.orderapi.dto.cartitem.WrappingCartItemDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.point.PointDTO;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentListener {
    // Todo 결제 성공 후 비동기적 상황 구현
    private static final Logger log = LoggerFactory.getLogger(PaymentListener.class);

    private final OrderGroupService orderGroupService;
    private final OrderDetailService orderDetailService;

    private final PointService pointService;

    private final MemberApiClient memberApiClient;
    private final BookCouponApiClient bookCouponApiClient;

    private final RetryStateService retryStateService;


    @RabbitListener(queues = "nhn24.order.queue")
    public void processSaveOrder(CombinedMessageDTO messageDTO, Channel channel, Message message) {
        // Todo 주문 내역 저장
        try {
            log.info("Saving Order...");
            WrappingCartItemDTO wrappingCartItemDTO = (WrappingCartItemDTO) messageDTO.getObject("WrappingCartItemDTO");
            List<CartItemDTO> cartItems = wrappingCartItemDTO.getCartItemList();
            if (cartItems.isEmpty()) {
                throw new RuntimeException();
            }
            OrderGroupCreateRequestDTO orderGroupCreateRequestDTO = (OrderGroupCreateRequestDTO) messageDTO.getObject("OrderGroupCreateRequestDTO");

            Long userId = (Long) messageDTO.getObject("UserId");

            // OrderGroup 생성
            OrderGroupResponseDTO orderGroupResponseDTO = orderGroupService.createOrderGroup(userId, orderGroupCreateRequestDTO);
            Long id = orderGroupResponseDTO.getId();

            //OrderDetail 저장
            for (CartItemDTO cartItem : cartItems) {
                OrderDetailCreateRequestDTO orderDetailCreateRequestDTO
                        = new OrderDetailCreateRequestDTO(
                        cartItem.getBookId(),
                        cartItem.getAmount(),
                        cartItem.getPrimePrice(),
                        cartItem.getDiscountPrice(),
                        id
                );
                orderDetailService.createOrderDetail(orderDetailCreateRequestDTO);
            }

            log.info("Completed Saving Order!!");

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            retryQueue(e, channel, message);
        }
    }

    @RabbitListener(queues = "nhn24.cart.queue")
    public void processClearCart(CombinedMessageDTO messageDTO, Channel channel, Message message) {
        // Todo 장바구니 초기화
        try {
            log.info("Clearing Cart...");

            WrappingCartItemDTO wrappingCartItemDTO = (WrappingCartItemDTO) messageDTO.getObject("WrappingCartItemDTO");
            List<Long> bookIds = new ArrayList<>();
            List<CartItemDTO> cartItems = wrappingCartItemDTO.getCartItemList();

            Long userId = (Long) messageDTO.getObject("UserId");

            for (CartItemDTO cartItem : cartItems) {
                bookIds.add(cartItem.getBookId());
            }

            CartUpdateRequestDTO cartUpdateRequestDTO = new CartUpdateRequestDTO(userId, bookIds);
            memberApiClient.updateCart(cartUpdateRequestDTO);
            // redis 주문서 데이터 초기화는 member-api 에서
            log.info("Completed Clearing Cart!!");

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            retryQueue(e, channel, message);
        }
    }

    @RabbitListener(queues = "nhn24.coupon.queue")
    public void useCoupon(CombinedMessageDTO messageDTO, Channel channel, Message message) {
        try {
            log.info("Used Coupon Update...");

            Long couponId = (Long) messageDTO.getObject("couponId");
            bookCouponApiClient.updateUseCoupon(couponId);

            log.info("Completed Update Coupon!!");
        } catch (Exception e) {
            retryQueue(e, channel, message);
        }
    }

    @RabbitListener(queues = "nhn24.point.queue")
    public void processPoint(CombinedMessageDTO messageDTO, Channel channel, Message message) {
        // Todo 구매 후 포인트 적립 및 사용
        try {
            log.info("Processing Point...");
            PointDTO pointDTO = (PointDTO) messageDTO.getObject("point");
            Long userId = (Long) messageDTO.getObject("userId");

            // 포인트 사용 및 적립
            if (pointDTO.getSpendPoint() > 0){
                pointService.createPointHistoryForPaymentSpend(userId, pointDTO.getSpendPoint());
            }
            pointService.createPointHistoryForPaymentEarn(userId, pointDTO.getEarnPoint());

            log.info("Completed Processing Point!!");
        } catch (Exception e) {
            retryQueue(e, channel, message);
        }
    }


    // DLQ 보내기 전에 큐 요청 재시도
    private void retryQueue(Exception e, Channel channel, Message message) {
        int count = retryStateService.getRetryCount(message.getMessageProperties().getReceivedRoutingKey()).incrementAndGet();

        try {
            if (count > 2) {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                retryStateService.resetRetryCount(message.getMessageProperties().getReceivedRoutingKey());
            }

            log.error("Try {}: Error processing message", count, e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);

        } catch (Exception nackException) {
            log.error("Error sending message", nackException);
        }
    }
}
