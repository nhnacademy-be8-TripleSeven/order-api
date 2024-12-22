package com.tripleseven.orderapi.business.pay.strategy;

import com.tripleseven.orderapi.business.pay.OrderProcessingStrategy;
import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.client.MemberApiClient;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// 추후 이벤트 결제 (배송비 무료 등) 상황 일 때 확장성 생각
public class NormalOrderProcessing implements OrderProcessingStrategy {
    private final OrderDetailService orderDetailService;
    private final OrderGroupService orderGroupService;
    private final BookCouponApiClient bookCouponApiClient;
    private final MemberApiClient memberApiClient;

    @Override
    public void processSingleOrder() {
    }

    @Override
    public void processMultipleOrder() {
        // Todo RabbitMQ 와 연결 (Listener 실행)

        // 1. 도서 API에서 가격 및 재고 확인

        // 2. 가격 및 재고 검증

        // 3. 주문 저장 (결제 요청을 보낼때 주문 그룹이 필요하다면 저장)
        // 결제가 어떻게 이루어지는지가 확인 해봐야할 듯,,

        // 4. RabbitMQ로 주문 성공 메시지 발행 (이 곳에서 결제 이후 과정 수행)
//        rabbitTemplate.convertAndSend("order.exchange", "order.routing.key" ,"");

        // 결제 버튼 누를 시: 재고 확인 -> 재고를 가지고 있어야하나? -> 결제 요청 -> 결제 성공
        // 결제 성공 시: rabbitmq -> 주문 그룹 생성 -> 주문 상세 생성 -> 결제(테이블) 생성
        // 순서 상관 없음: 포인트 적립 , 장바구니 초기화 , 성공 알림

        // 주문 번호를 가져오기 위해 미리 주문 그룹을 생성 해놓을지 고민,,

        /* 예상 책 수량 정보 및 가격
        List<CartItem> cartItems = bookCouponApiClient.getBookPriceList(orderDetailCreateRequest.getBookId());
        long totalPrice = 0L;
        for(CartItem cartItem : cartItems){
            totalPrice += cartItem.getDiscountPrice() * cartItem.getAmount();
        }
        */

    }
}
