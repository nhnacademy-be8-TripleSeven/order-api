package com.tripleseven.orderapi.client;

import com.tripleseven.orderapi.dto.cartitem.CartUpdateRequestDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "member-api")
public interface MemberApiClient {
    @PostMapping("/members/cartClear")
    void updateCart(@Valid @RequestBody CartUpdateRequestDTO cartUpdateRequestDTO);
}
