package com.tripleseven.orderapi.client;

import com.tripleseven.orderapi.dto.cartitem.CartItemDTO;
import com.tripleseven.orderapi.dto.coupon.CouponDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "book-coupon-api")
public interface BookCouponApiClient {

    @PostMapping("/coupons/use/{couponId}")
    void updateUseCoupon(@PathVariable("couponId") Long couponId);

    @GetMapping("/coupons/{couponId}/coupon-polities")
    CouponDTO getCoupon(@PathVariable("couponId") Long couponId);

    @PostMapping("/books/cart")
    List<CartItemDTO> getCartItems(@RequestBody List<Long> bookIds);

    @GetMapping("/books/{bookId}/name")
    String getBookName(@PathVariable("bookId") Long bookId);
}
