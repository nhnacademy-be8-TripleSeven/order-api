package com.example.orderapi.business.pay.strategy;

import com.example.orderapi.client.BookCouponApiClient;
import com.example.orderapi.dto.orderdetail.OrderDetailCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupResponse;
import com.example.orderapi.service.orderdetail.OrderDetailService;
import com.example.orderapi.service.ordergroup.OrderGroupService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NormalOrderProcessing  {
    private final OrderDetailService orderDetailService;
    private final OrderGroupService orderGroupService;
    private final BookCouponApiClient bookCouponApiClient;

    public void processOrder(OrderGroupCreateRequest orderGroupCreateRequest, OrderDetailCreateRequest orderDetailCreateRequest) {
        OrderGroupResponse orderGroupResponse = orderGroupService.createOrderGroup(orderGroupCreateRequest);
        Long orderGroupId = orderGroupResponse.getId();

        orderDetailService.createOrderDetail(orderDetailCreateRequest);
//        List<CartItem> cartItems = bookCouponApiClient.getBookPriceList(orderDetailCreateRequest.getBookId());
//        long totalPrice = 0L;
//        for(CartItem cartItem : cartItems){
//            totalPrice += cartItem.getDiscountPrice() * cartItem.getAmount();
//        }


        // 결제
//        processPayment(totalPrice);

        // 포인트 적립
        processPointEarning();

    }

    public void processPayment(Long price) {}

    public void processPointEarning() {}

}
