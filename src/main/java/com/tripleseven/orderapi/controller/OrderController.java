package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.order.process.OrderProcessing;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderProcessing orderProcessing;

    @PostMapping("/api/orders/process")
    public ResponseEntity<Void> saveOrderHistory(
            @RequestHeader(value = "X-USER", required = false) Long userId,
            @CookieValue("GUEST-ID") String guestId
    ) {
        if (userId != null) {
            orderProcessing.processMemberOrder(userId);
        } else {
            orderProcessing.processNonMemberOrder(guestId);
        }
        return ResponseEntity.ok().build();
    }
}
