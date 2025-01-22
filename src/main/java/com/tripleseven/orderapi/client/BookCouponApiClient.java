package com.tripleseven.orderapi.client;

import com.tripleseven.orderapi.dto.book.BookStockRequestDTO;
import com.tripleseven.orderapi.dto.coupon.CouponDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "book-coupon-api")
public interface BookCouponApiClient {

    @PutMapping("/coupons/{coupon-id}/use")
    void useCoupon(@PathVariable("coupon-id") Long couponId);

    @GetMapping("/coupons/{coupon-id}/coupon-polities")
    CouponDTO getCoupon(@PathVariable("coupon-id") Long couponId);

    @GetMapping("/books/{book-id}/name")
    String getBookName(@PathVariable("book-id") Long bookId);

    @GetMapping("/api/coupons/apply")
    Long applyCoupon(
            @RequestParam Long couponId,
            @RequestParam Long paymentAmount);

    @PutMapping("/books/stock-reduce")
    void reduceStock(@RequestBody BookStockRequestDTO bookStockRequestDTO);
}
