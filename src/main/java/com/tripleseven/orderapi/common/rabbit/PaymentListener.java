package com.tripleseven.orderapi.common.rabbit;

import com.tripleseven.orderapi.business.pointservice.PointService;
import com.tripleseven.orderapi.client.MemberApiClient;
import com.tripleseven.orderapi.dto.WrappingMessageObject;
import com.tripleseven.orderapi.dto.cartitem.CartItemDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentListener {
    // Todo 결제 성공 후 비동기적 상황 구현
    private static final Logger log = LoggerFactory.getLogger(PaymentListener.class);


    private final OrderGroupService orderGroupService;
    private final OrderDetailService orderDetailService;
    private final MemberApiClient memberApiClient;
    private final PointService pointService;


    @RabbitListener(queues = "nhn24.order.queue")
    public void processSaveOrder(WrappingMessageObject messageObject) {
        // Todo 주문 내역 저장
        log.info("Saving Order...");
        List<Object> objects = messageObject.getObjectList();

        List<CartItemDTO> cartItems = (List<CartItemDTO>) objects.getFirst();
        OrderGroupCreateRequestDTO orderGroupCreateRequestDTO = (OrderGroupCreateRequestDTO) objects.get(1);
        // OrderGroup 생성
        OrderGroupResponseDTO orderGroupResponseDTO = orderGroupService.createOrderGroup(orderGroupCreateRequestDTO);
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
    }

    @RabbitListener(queues = "nhn24.cart.queue")
    public void processClearCart(List<CartItemDTO> cartItems) {
        // Todo 장바구니 초기화
        log.info("Clearing Cart...");
        List<Long> bookIds = new ArrayList<>();
        for (CartItemDTO cartItem : cartItems) {
            bookIds.add(cartItem.getBookId());
        }
        memberApiClient.updateCart(bookIds);
        log.info("Completed Clearing Cart!!");
    }

    @RabbitListener(queues = "nhn24.point.queue")
    public void processPoint(int earn, int spend) {
        // Todo 포인트 적립 및 사용
        log.info("Processing Point...");
        // Todo 포인트 서비스 생성해서 구현,,,
//        pointService.createPointHistoryForPaymentEarn(earn);
//        pointService.createPointHistoryForPaymentSpend(spend);
        log.info("Completed Processing Point");
    }
}
