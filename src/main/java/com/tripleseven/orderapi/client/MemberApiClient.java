package com.tripleseven.orderapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "member-api")
public interface MemberApiClient {
    @PostMapping("/cart/book")
    void deleteCart(
            @RequestHeader(value = "X-USER") Long userId,
            @CookieValue("X-GUEST") String guestId,
            @RequestParam Long bookId);

    @GetMapping("/members/grade/point")
    Long getGradePoint(
            @RequestHeader(value = "X-USER") Long userId,
            @RequestParam Long amount);
}
