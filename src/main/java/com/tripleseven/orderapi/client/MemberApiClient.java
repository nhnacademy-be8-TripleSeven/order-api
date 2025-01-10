package com.tripleseven.orderapi.client;

import com.tripleseven.orderapi.dto.cartitem.CartUpdateRequestDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "member-api")
public interface MemberApiClient {
    @PostMapping("/cart/book")
    void updateCart(
            @RequestHeader("X-USER") Long userId,
            @RequestParam Long bookId);
}
