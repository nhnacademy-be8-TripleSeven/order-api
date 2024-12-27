package com.tripleseven.orderapi.client;

import com.tripleseven.orderapi.dto.cartitem.CartItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "book-coupon-api")
public interface BookCouponApiClient {
    @GetMapping("/books/price")
    List<CartItemDTO> getBookPriceList(List<Long> bookIds);
}
