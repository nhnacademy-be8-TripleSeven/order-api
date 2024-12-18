package com.example.orderapi.rabbit;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentListener.class);

    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "nhn24.order.queue")
    public void processPayment(String message) {
        // 결제 처리
        log.info(message);
        // 성공 시 실행
            // 주문 상태 업데이트

            // 포인트 적립 처리

        // 사용자 알림 (성공, 실패)
            // 결제 성공 메시지 전송 dooray hook

        // 별개 구현 (성공 시 만 실행)
        // 장바구니 초기화
    }

    @RabbitListener(queues = "nhn24.cart.queue")
    public void clearCart() {
        // 장바구니 초기화 로직
//        memberApiClient.clearCart(userId, cartId);
    }

    @RabbitListener(queues = "nhn24.point.queue")
    public void processPoint() {

    }
}
