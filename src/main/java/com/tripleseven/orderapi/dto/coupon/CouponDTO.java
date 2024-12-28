package com.tripleseven.orderapi.dto.coupon;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CouponDTO {
    Long couponMinAmount;

    Long couponMaxAmount;

    BigDecimal couponDiscountRate;

    Long couponDiscountAmount;

    CouponStatus couponStatus;
}
