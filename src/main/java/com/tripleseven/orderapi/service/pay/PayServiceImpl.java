package com.tripleseven.orderapi.service.pay;

import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.cartitem.OrderItemDTO;
import com.tripleseven.orderapi.dto.coupon.CouponDTO;
import com.tripleseven.orderapi.dto.coupon.CouponStatus;
import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
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

import java.util.*;

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
    public void createPay(Long userId, JSONObject jsonObject) {
        Pay pay = new Pay();
        PayInfoDTO infoDto = (PayInfoDTO) redisTemplate.opsForHash().get(userId.toString(), "OrderInfo");
        //infoDTO를 각 db에 저장해야함

        pay.ofCreate(jsonObject);
        payRepository.save(pay);
    }

    @Override
    public void cancelPay(JSONObject response) {
        Pay pay = payRepository.findByPaymentKey(response.get("paymentKey").toString());

        if (Objects.isNull(pay)) {
            throw new CustomException(ErrorCode.PAY_NOT_FOUND);
        }

        pay.ofUpdate(response);
    }

    @Override
    public PayInfoResponseDTO createPayInfo(Long userId, String guestId, PayInfoRequestDTO request) {
        long orderId = UUID.randomUUID().getMostSignificantBits();
        PayInfoDTO payInfoDTO = new PayInfoDTO();
        payInfoDTO.ofCreate(orderId, request);

//        checkValid(userId, payInfoDTO);

        // TODO 검증 후 저장 (수정 해야됨)
        if (Objects.nonNull(userId)) {
            redisTemplate.opsForHash().put(userId.toString(), "PayInfo", payInfoDTO);
        } else {
            redisTemplate.opsForHash().put(guestId, "PayInfo", payInfoDTO);
        }
        return new PayInfoResponseDTO(orderId, request.getTotalAmount());
    }

    @Override
    public OrderPayInfoDTO getOrderPayInfo(Long orderId) {

        return payRepository.getDTOByOrderGroupId(orderId);
    }

    private void checkValid(Long userId, PayInfoDTO payInfo) {
        List<OrderBookInfoDTO> bookInfos = payInfo.getBookOrderDetails();
        Long couponId = payInfo.getCouponId();
        Long usePoint = payInfo.getPoint();
        Long totalAmount = payInfo.getTotalAmount();


        Map<Long, Integer> bookAmounts = new HashMap<>();
        List<Long> bookIds = new ArrayList<>();

        for (OrderBookInfoDTO bookInfo : bookInfos) {
            bookIds.add(bookInfo.getBookId());
            bookAmounts.put(bookInfo.getBookId(), bookInfo.getQuantity());
        }

        List<OrderItemDTO> realItems = bookCouponApiClient.getCartItems(bookIds);

        // 재고 검증
        checkAmount(bookAmounts, realItems);

        // 쿠폰 검증
        checkCoupon(couponId, bookInfos);

        // 포인트 검증
        checkPoint(userId, totalAmount, usePoint);
    }

    private void checkAmount(Map<Long, Integer> bookAmounts, List<OrderItemDTO> realItems) {
        for (OrderItemDTO realItem : realItems) {
            int amount = bookAmounts.get(realItem.getBookId());

            if (realItem.getAmount() > amount) {
                throw new CustomException(ErrorCode.AMOUNT_FAILED_CONFLICT);
            }
        }
    }

    private void checkCoupon(Long totalAmount, List<OrderBookInfoDTO> bookInfos) {
        CouponDTO coupon = bookCouponApiClient.getCoupon(1L);
        for (OrderBookInfoDTO bookInfo : bookInfos) {
            // bookInfo.getCouponId() != null
//            Long realDiscount = bookCouponApiClient.applyCoupon(1L, bookInfo.getPrice());
        }


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
//        if (!discountAmount.equals(realDiscount)) {
//            throw new CustomException(ErrorCode.COUPON_USED_UNPROCESSABLE_ENTITY);
//        }

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




