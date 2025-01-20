package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.service.pay.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PayController {
    private final PayService payService;
    @GetMapping("/orders/pay/{order-id}")
    public ResponseEntity<Long> getPayPrice(@PathVariable("order-id") Long orderId) {
        Long payPrice = payService.getPayPrice(orderId);
        return ResponseEntity.ok(payPrice);
    }
}
