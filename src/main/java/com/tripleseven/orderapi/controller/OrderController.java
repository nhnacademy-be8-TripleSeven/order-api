package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.pay.OrderProcessingStrategy;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderProcessingStrategy orderProcessingStrategy;

    @PostMapping("/api/orders/process")
    public ResponseEntity<Void> saveOrderHistory(
            @RequestHeader(value = "X-USER", required = false) Long userId,
            @CookieValue("GUEST-ID") String guestId,
            @RequestBody OrderGroupCreateRequestDTO request
    ){
        if(userId != null){
            orderProcessingStrategy.processMemberOrder(userId, request);
        }
        else{
            orderProcessingStrategy.processNonMemberOrder(guestId, request);
        }
        return ResponseEntity.ok().build();
    }
}
