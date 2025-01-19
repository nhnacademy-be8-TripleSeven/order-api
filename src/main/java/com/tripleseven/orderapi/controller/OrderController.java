package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/orders/amount/net")
    public ResponseEntity<Long> getNetAmount(@RequestParam Long userId) {
        Long amount = orderService.getThreeMonthsNetAmount(userId);
        return ResponseEntity.ok(amount);
    }
}
