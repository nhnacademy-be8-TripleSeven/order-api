package com.tripleseven.orderapi.business.pay.strategy;

import com.tripleseven.orderapi.business.pay.OrderProcessingStrategy;
import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.CombinedMessageDTO;
import com.tripleseven.orderapi.dto.cartitem.CartItemDTO;
import com.tripleseven.orderapi.dto.coupon.CouponDTO;
import com.tripleseven.orderapi.dto.coupon.CouponStatus;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.point.PointDTO;
import com.tripleseven.orderapi.service.pointhistory.PointHistoryService;
import com.tripleseven.orderapi.service.pointpolicy.PointPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
// 추후 이벤트 결제 (배송비 무료 등) 상황 일 때 확장성 생각
public class NormalOrderProcessing implements OrderProcessingStrategy {
    private static final String MEMBER_CART_KEY_PREFIX = "cart:member:";
    private static final String GUEST_CART_KEY_PREFIX = "cart:guest:";

    private static final String EXCHANGE_NAME = "nhn24.pay.exchange";

    private static final String ORDER_ROUTING_KEY = "order.routing.key";
    private static final String POINT_ROUTING_KEY = "point.routing.key";
    private static final String CART_ROUTING_KEY = "cart.routing.key";
    private static final String COUPON_ROUTING_KEY = "coupon.routing.key";

    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private final BookCouponApiClient bookCouponApiClient;

    private final PointHistoryService pointHistoryService;
    private final PointPolicyService pointPolicyService;

    @Override
    public void processNonMemberOrder(String guestId, OrderGroupCreateRequestDTO orderGroupCreateRequestDTO) {
        // Todo 비회원 주문
        orderProcessing(Long.valueOf(guestId), orderGroupCreateRequestDTO, false);

    }

    @Override
    public void processMemberOrder(Long userId, OrderGroupCreateRequestDTO orderGroupCreateRequestDTO) {
        // Todo 회원 주문
        orderProcessing(userId, orderGroupCreateRequestDTO, true);
        couponProcessing(userId);
    }

    // 주문 저장 및 장바구니 초기화
    private void orderProcessing(Long userId, OrderGroupCreateRequestDTO dto, Boolean isMember) {
        String CART_KEY_PREFIX = userId != 0L ? MEMBER_CART_KEY_PREFIX : GUEST_CART_KEY_PREFIX;

        String cartKey = CART_KEY_PREFIX.concat(userId.toString());
        HashOperations<String, String, CartItemDTO> hashOps = redisTemplate.opsForHash();
        Map<String, CartItemDTO> cartItemMap = hashOps.entries(cartKey);


        // TODO 주문 데이터에서 가져온 책만 지워야됨(비교 후 삭제)
        List<String> bookIds = (List<String>) hashOps.keys(cartKey);
        redisTemplate.opsForHash().delete(cartKey, "bookIds");

        CombinedMessageDTO orderMessageDTO = new CombinedMessageDTO();
        orderMessageDTO.addObject("CartItemMap", cartItemMap);
        orderMessageDTO.addObject("UserId", userId);
        orderMessageDTO.addObject("OrderGroupCreateRequestDTO", dto);
        orderMessageDTO.addObject("isMember",isMember);

        if (isMember) {
            PointDTO point = (PointDTO) redisTemplate.opsForHash().get(userId.toString(), "point");
            orderMessageDTO.addObject("point", point);
        }
        // TODO 결제 정보 저장
        orderMessageDTO.addObject("PaymentInfo", "paymentInfo");

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ORDER_ROUTING_KEY, orderMessageDTO);


        CombinedMessageDTO cartMessageDTO = new CombinedMessageDTO();
        cartMessageDTO.addObject("BookIds", bookIds);
        cartMessageDTO.addObject("UserId", userId);


        rabbitTemplate.convertAndSend(EXCHANGE_NAME, CART_ROUTING_KEY, cartMessageDTO);
    }

    // 쿠폰 사용
    private void couponProcessing(Long userId) {
        Long couponId = (Long) redisTemplate.opsForHash().get(userId.toString(), "order");

        CombinedMessageDTO couponMessageDTO = new CombinedMessageDTO();
        couponMessageDTO.addObject("couponId", couponId);
        couponMessageDTO.addObject("userId", userId);

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, COUPON_ROUTING_KEY, couponMessageDTO);
    }


    // 결제 요청 하기 전 체크 할 사항들 (임시)
    private boolean checkValid(Long userId) {

        List<CartItemDTO> cartItems = (List<CartItemDTO>) redisTemplate.opsForHash().get(userId.toString(), "CartItems");
        Long couponId = (Long) redisTemplate.opsForHash().get(userId.toString(), "order");
        CouponDTO coupon = bookCouponApiClient.getCoupon(couponId);
        PointDTO point = (PointDTO) redisTemplate.opsForHash().get(userId.toString(), "point");

        List<Long> bookIds = new ArrayList<>();
        for (CartItemDTO cartItem : cartItems) {
            bookIds.add(cartItem.getBookId());
        }

        // 1. 도서 API에서 가격 및 재고 확인
        List<CartItemDTO> realItems = bookCouponApiClient.getCartItems(bookIds);

        for (CartItemDTO cartItem : realItems) {
        }

        int price = 0;
        int totalPrice = 0;


        int memberPoint = pointHistoryService.getTotalPointByMemberId(userId);

        // 포인트 사용량 체크
        if (Objects.nonNull(point)) {
            // 원래 보유 포인트보다 높은 경우
            if (memberPoint != point.getTotalPoint()) {
                throw new RuntimeException();
            }

            // 보유 포인트보다 사용량이 더 큰 경우
            if (memberPoint < point.getSpendPoint()) {
                throw new RuntimeException();
            }

            // 적립량이 맞지 않는 경우
            BigDecimal earnPercent = pointPolicyService.findById(1L).getRate();
            int earnPoint = earnPercent.multiply(BigDecimal.valueOf(price)).intValue();
//            if (point.getEarnPoint() != earnPoint) {
//                throw new RuntimeException();
//            }


            // 최종 가격보다 사용량이 더 큰 경우
            if (totalPrice > point.getSpendPoint()) {
                throw new RuntimeException();
            }

            CombinedMessageDTO pointMessageDTO = new CombinedMessageDTO();
            pointMessageDTO.addObject("point", point);

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, POINT_ROUTING_KEY, pointMessageDTO);
        }

        if (Objects.nonNull(coupon)) {
            if (!coupon.getCouponStatus().equals(CouponStatus.NOTUSED)) {
                throw new RuntimeException();
            }

            CombinedMessageDTO couponMessageDTO = new CombinedMessageDTO();
            couponMessageDTO.addObject("coupon", coupon);

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, COUPON_ROUTING_KEY, couponMessageDTO);
        }
        return false;
    }
}
