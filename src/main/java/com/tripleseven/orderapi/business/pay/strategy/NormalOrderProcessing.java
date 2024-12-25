package com.tripleseven.orderapi.business.pay.strategy;

import com.tripleseven.orderapi.business.pay.OrderProcessingStrategy;
import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.client.MemberApiClient;
import com.tripleseven.orderapi.dto.WrappingMessageObject;
import com.tripleseven.orderapi.dto.cartitem.CartItemDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.service.pointhistory.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
// 추후 이벤트 결제 (배송비 무료 등) 상황 일 때 확장성 생각
public class NormalOrderProcessing implements OrderProcessingStrategy {
    private static final String EXCHANGE_NAME = "nhn24.pay.exchange";

    private static final String ORDER_ROUTING_KEY = "order.routing.key";
    private static final String POINT_ROUTING_KEY = "point.routing.key";
    private static final String CART_ROUTING_KEY = "cart.routing.key";

    private final PointHistoryService pointHistoryService;
    private final BookCouponApiClient bookCouponApiClient;
    private final MemberApiClient memberApiClient;
    private RabbitTemplate rabbitTemplate;
    private RedisTemplate<Long, String> redisTemplate;

    @Override
    public void processSingleOrder(OrderGroupCreateRequestDTO orderGroupCreateRequestDTO) {
        // Todo 단일 주문
        List<CartItemDTO> cartItems = (List<CartItemDTO>) redisTemplate.opsForHash().get(1L, "CartItems");

        WrappingMessageObject orderObject = new WrappingMessageObject();
        orderObject.addWrappingObjects(cartItems, orderGroupCreateRequestDTO);
//        int userPoint = pointHistoryService.getTotalPointByMemberId(1L);

        // 검증 이후
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ORDER_ROUTING_KEY);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, POINT_ROUTING_KEY);
    }

    @Override
    public void processMultipleOrder(OrderGroupCreateRequestDTO orderGroupCreateRequestDTO) {
        // Todo 장바구니 주문
        //  RabbitMQ 와 연결 (Listener 실행)
        //  주문 버튼 누르면 Redis에 저장된 장바구니 데이터 가져옴
        List<CartItemDTO> cartItems = (List<CartItemDTO>) redisTemplate.opsForHash().get(1L, "CartItems");

        //        Message message = MessageBuilder
//                .withBody("Message body".getBytes())
//                .setHeader("header1", "value1")
//                .setHeader("header2", 42)
//                .build();
        // Todo Object 화 해서 RabbitMQ에 보내줌
        WrappingMessageObject orderObject = new WrappingMessageObject();
        orderObject.addWrappingObjects(cartItems, orderGroupCreateRequestDTO);

        // 1. 도서 API에서 가격 및 재고 확인
//        int price = 0;

        // memberId , CartId, CartItem

        // 2. 포인트, 쿠폰 체크
//        int userPoint = pointHistoryService.getTotalPointByMemberId(1L);


        // 검증 이후
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ORDER_ROUTING_KEY, orderObject);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, CART_ROUTING_KEY);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, POINT_ROUTING_KEY);
    }
}
