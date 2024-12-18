package com.example.orderapi.business.pay;

import com.example.orderapi.client.BookCouponApiClient;
import com.example.orderapi.client.MemberApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

//    private final BookCouponApiClient bookCouponApiClient;
//    private final MemberApiClient memberApiClient;
//    private final RabbitTemplate rabbitTemplate;

        // @Transactional(rollbackFor = { PaymentFailedException.class, InsufficientStockException.class })
    public void processOrder() {
        // 1. 도서 API에서 가격 및 재고 확인

        // 2. 가격 및 재고 검증

        // 3. 주문 저장 (결제 요청을 보낼때 주문 그룹이 필요하다면 저장)
            // 결제가 어떻게 이루어지는지가 확인 해봐야할 듯,,

        // 4. RabbitMQ로 주문 성공 메시지 발행 (이 곳에서 결제 이후 과정 수행)
//        rabbitTemplate.convertAndSend("order.exchange", "order.routing.key" ,"");

        // 주문 번호를 가져오기 위해 미리 주문 그룹을 생성 해놓을지 고민,,

        // 개발환경에 RabbitMQ 서버가 있는데 여기에 있는걸 쓰는건지
    }
}
