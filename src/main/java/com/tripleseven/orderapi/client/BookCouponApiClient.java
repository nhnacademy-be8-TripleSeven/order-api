package com.tripleseven.orderapi.client;

import com.tripleseven.orderapi.dto.cartitem.CartItemDTO;
import com.tripleseven.orderapi.dto.cartitem.OrderItemDTO;
import com.tripleseven.orderapi.dto.coupon.CouponDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "book-coupon-api")
public interface BookCouponApiClient {

    @PostMapping("/coupons/use/{couponId}")
    void updateUseCoupon(@PathVariable("couponId") Long couponId);

    @GetMapping("/coupons/{couponId}/coupon-polities")
    CouponDTO getCoupon(@PathVariable("couponId") Long couponId);

    @PostMapping("/books/cart")
    List<OrderItemDTO> getCartItems(@RequestBody List<Long> bookIds);

    @GetMapping("/books/{bookId}/name")
    String getBookName(@PathVariable("bookId") Long bookId);

    @PostMapping("/coupons/apply")
    Long applyCoupon(
            @RequestParam Long couponId,
            @RequestParam Long paymentAmount);
}
