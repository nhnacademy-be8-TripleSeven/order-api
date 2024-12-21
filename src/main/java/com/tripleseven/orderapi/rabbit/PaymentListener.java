package com.tripleseven.orderapi.rabbit;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentListener {
    // Todo 결제 성공 후 비동기적 상황 구현
    private static final Logger log = LoggerFactory.getLogger(PaymentListener.class);

    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "nhn24.order.queue")
    public void processOrder(String message) {
        log.info(message);
        // Todo 주문 내역 저장
    }

    @RabbitListener(queues = "nhn24.cart.queue")
    public void clearCart() {
        // Todo 장바구니 초기화
    }

    @RabbitListener(queues = "nhn24.point.queue")
    public void processPoint() {
        // Todo 포인트 적립 및 사용
    }
}
