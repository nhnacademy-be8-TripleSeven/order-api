package com.example.orderapi.business.pay.strategy;

import com.example.orderapi.client.BookCouponApiClient;
import com.example.orderapi.client.MemberApiClient;
import com.example.orderapi.dto.orderdetail.OrderDetailCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupResponse;
import com.example.orderapi.service.orderdetail.OrderDetailService;
import com.example.orderapi.service.ordergroup.OrderGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NormalOrderProcessing implements OrderProcessingStrategy{
    private final OrderDetailService orderDetailService;
    private final OrderGroupService orderGroupService;
    private final BookCouponApiClient bookCouponApiClient;
    private final MemberApiClient memberApiClient;

    @Override
    public void processSingleOrder() {}

    @Override
    public void processMultipleOrder() {
        // 1. 도서 API에서 가격 및 재고 확인

        // 2. 가격 및 재고 검증

        // 3. 주문 저장 (결제 요청을 보낼때 주문 그룹이 필요하다면 저장)
        // 결제가 어떻게 이루어지는지가 확인 해봐야할 듯,,

        // 4. RabbitMQ로 주문 성공 메시지 발행 (이 곳에서 결제 이후 과정 수행)
//        rabbitTemplate.convertAndSend("order.exchange", "order.routing.key" ,"");

        // 주문 번호를 가져오기 위해 미리 주문 그룹을 생성 해놓을지 고민,,
//        List<CartItem> cartItems = bookCouponApiClient.getBookPriceList(orderDetailCreateRequest.getBookId());
//        long totalPrice = 0L;
//        for(CartItem cartItem : cartItems){
//            totalPrice += cartItem.getDiscountPrice() * cartItem.getAmount();
//        }
        

    }

}
