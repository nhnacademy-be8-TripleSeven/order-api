package com.tripleseven.orderapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "member-api")
public interface MemberApiClient {
    @PostMapping("/cart/book")
    void deleteCart(
            @RequestHeader(value = "X-USER") Long userId,
            @RequestParam Long bookId);

    @GetMapping("/members/grade/point")
    Integer getGradePoint(@RequestHeader(value = "X-USER") Long userId);
}
