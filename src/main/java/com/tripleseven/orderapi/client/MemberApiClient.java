package com.tripleseven.orderapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "member-api")
public interface MemberApiClient {
    @DeleteMapping("/cart/book")
    void deleteCart(
            @RequestHeader(value = "X-USER", required = false) Long userId,
            @CookieValue("GUEST-ID") String guestId,
            @RequestParam Long bookId);

    @GetMapping("/members/grade/point")
    Long getGradePoint(
            @RequestHeader("X-USER") Long userId,
            @RequestParam Long amount);
}
