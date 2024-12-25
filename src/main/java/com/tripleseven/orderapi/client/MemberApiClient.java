package com.tripleseven.orderapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "member-api")
public interface MemberApiClient {
    @PostMapping("/members/cartClear")
    void updateCart(List<Long> bookIds);
}
