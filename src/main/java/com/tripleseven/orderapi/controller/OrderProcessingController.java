package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.order.process.OrderProcessing;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderProcessingController {
    private final OrderProcessing orderProcessing;

    @PostMapping("/orders/pay-complete")
    public ResponseEntity<Void> orderSingleProcessing(@RequestHeader(value = "X-User", required = false) Long userId,
                                                      @CookieValue(value = "X-GUEST") String guestId) {
        if (userId != null) {
            orderProcessing.processMemberOrder(userId);
        } else {
            orderProcessing.processNonMemberOrder(guestId);
        }

        return ResponseEntity.ok().build();
    }

}
