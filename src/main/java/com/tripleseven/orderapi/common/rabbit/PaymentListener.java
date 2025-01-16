package com.tripleseven.orderapi.common.rabbit;

import com.rabbitmq.client.Channel;
import com.tripleseven.orderapi.business.point.PointService;
import com.tripleseven.orderapi.client.MemberApiClient;
import com.tripleseven.orderapi.dto.CombinedMessageDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentListener.class);

    private final MemberApiClient memberApiClient;
    private final RetryStateService retryStateService;
    private final PointService pointService;

    @RabbitListener(queues = "nhn24.cart.queue")
    public void processClearCart(CombinedMessageDTO messageDTO, Channel channel, Message message) {
        // 장바구니 초기화 (구매한 상품 대상)
        try {
            log.info("Clearing Cart...");

            List<Long> bookIdsS = (List<Long>) messageDTO.getObject("BookIds");
            String userId = (String) messageDTO.getObject("UserId");

            // 여러번 호출
            bookIdsS.stream()
                    .forEach(bookId -> memberApiClient.deleteCart(Long.valueOf(userId), bookId));

            log.info("Completed Clearing Cart!!");

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            retryQueue(e, channel, message);
        }
    }

    @RabbitListener(queues = "nhn24.point.queue")
    public void usePoint(CombinedMessageDTO messageDTO, Channel channel, Message message) {
        try {
            log.info("Used Point save...");

            Long userId = (Long) messageDTO.getObject("UserId");
            Long totalAmount = (Long) messageDTO.getObject("TotalAmount");
            Long orderId = (Long) messageDTO.getObject("OrderId");

            pointService.createPointHistoryForPaymentEarn(userId, totalAmount, orderId);

            log.info("Completed Point save!!");
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
