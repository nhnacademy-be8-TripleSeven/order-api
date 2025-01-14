package com.tripleseven.orderapi.service.pay;

import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.cartitem.CartItemDTO;
import com.tripleseven.orderapi.dto.coupon.CouponDTO;
import com.tripleseven.orderapi.dto.coupon.CouponStatus;
import com.tripleseven.orderapi.dto.order.OrderPayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoResponseDTO;
import com.tripleseven.orderapi.entity.pay.Pay;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.pay.PayRepository;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.pointhistory.PointHistoryService;
import com.tripleseven.orderapi.service.pointpolicy.PointPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {
    private final PayRepository payRepository;
    private final OrderGroupService orderGroupService;
    private final PointHistoryService pointHistoryService;
    private final PointPolicyService pointPolicyService;

    private final BookCouponApiClient bookCouponApiClient;

    private final RedisTemplate<String, Object> redisTemplate;

    // TODO save 해서 반환받아야할 경우가 있는지 확인
    //  저장되는 값 더 추가해야됨
    @Override
    public void save(Long userId, JSONObject jsonObject) {
        Pay pay = new Pay();
        PayInfoDTO infoDto = (PayInfoDTO) redisTemplate.opsForHash().get(userId.toString(), "OrderInfo");
        //infoDTO를 각 db에 저장해야함

        pay.ofCreate(jsonObject);
        payRepository.save(pay);
    }

    @Override
    public void payCancel(JSONObject response) {
        Pay pay = payRepository.findByPaymentKey(response.get("paymentKey").toString());

        if (Objects.isNull(pay)) {
            throw new CustomException(ErrorCode.PAY_NOT_FOUND);
        }

        pay.ofUpdate(response);
    }

    @Override
    public PayInfoResponseDTO getPayInfo(Long userId, PayInfoRequestDTO request) {
        long orderId = UUID.randomUUID().getMostSignificantBits();
        PayInfoDTO payInfoDTO = new PayInfoDTO();
        payInfoDTO.ofCreate(orderId, request);

        checkValid(userId, payInfoDTO);

        // TODO 검증 후 저장
        redisTemplate.opsForHash().put(userId.toString(), "OrderInfo", payInfoDTO);
        return new PayInfoResponseDTO(orderId, request.getTotalAmount());
    }

    public OrderPayInfoDTO getOrderPayInfo(Long orderId) {

        return payRepository.getDTOByOrderGroupId(orderId);
    }

    private void checkValid(Long userId, PayInfoDTO payInfo) {
        List<CartItemDTO> cartItems = (List<CartItemDTO>) redisTemplate.opsForHash().get(userId.toString(), "CartItems");
        Long couponId = payInfo.getCouponId();
        Long usePoint = payInfo.getPoint();
        Long totalAmount = payInfo.getTotalAmount();

        List<Long> bookIds = new ArrayList<>();

        for (CartItemDTO cartItem : cartItems) {
            bookIds.add(cartItem.getBookId());
        }

        // 재고 검증
        checkAmount(bookIds);

        // 쿠폰 검증
        checkCoupon(couponId, totalAmount, 0L);

        // 포인트 검증
        checkPoint(userId, totalAmount, usePoint);
    }

    private void checkAmount(List<Long> bookIds) {
        // 재고 확인
        List<CartItemDTO> realItems = bookCouponApiClient.getCartItems(bookIds);

        for (CartItemDTO cartItem : realItems) {
            // 재고 비교
            cartItem.getQuantity();
        }

    }

    private void checkCoupon(Long couponId, Long totalAmount, Long discountAmount) {
        CouponDTO coupon = bookCouponApiClient.getCoupon(couponId);
        Long discount = bookCouponApiClient.applyCoupon(couponId, totalAmount);

        // 쿠폰 존재 검증
        if (Objects.isNull(coupon)) {
            throw new CustomException(ErrorCode.COUPON_NOT_FOUND);
        }

        // 사용 가능한 쿠폰 확인
        if (!coupon.getCouponStatus().equals(CouponStatus.NOTUSED)) {
            throw new CustomException(ErrorCode.COUPON_USED_UNPROCESSABLE_ENTITY);
        }

        // 최소 사용 금액보다 적음
        if (totalAmount < coupon.getCouponMinAmount()) {
            throw new CustomException(ErrorCode.COUPON_USED_UNPROCESSABLE_ENTITY);
        }

        // 계산된 할인 금액과 맞지 않음
        if(!discountAmount.equals(discount)) {
            throw new CustomException(ErrorCode.COUPON_USED_UNPROCESSABLE_ENTITY);
        }

    }

    private Long checkPoint(Long userId, Long totalPrice, Long point) {
        int userPoint = pointHistoryService.getTotalPointByMemberId(userId);

        // 보유 포인트보다 포인트 사용량이 더 큰 경우
        if (userPoint < point) {
            throw new CustomException(ErrorCode.POINT_UNPROCESSABLE_ENTITY);
        }

        // 최종 가격보다 사용량이 더 큰 경우
        if (point > totalPrice) {
            point = point - totalPrice;
        }

        return point;
    }
}




