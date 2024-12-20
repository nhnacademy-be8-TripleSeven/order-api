package com.tripleseven.orderapi.client;

import com.tripleseven.orderapi.domain.CartItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "book-coupon-api")
public interface BookCouponApiClient {
    @GetMapping("/api/books/price")
    List<CartItem> getBookPriceList(List<Long> bookIds);
}
