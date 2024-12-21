package com.tripleseven.orderapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "member-api")
public interface MemberApiClient {
    @GetMapping("/api/members")
    String getMember();
}
