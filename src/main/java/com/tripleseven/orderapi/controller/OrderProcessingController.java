package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.pay.OrderProcessingStrategy;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderProcessingController {
    private final OrderProcessingStrategy orderProcessingStrategy;

    @PostMapping("/orders/pay-complete")
    public ResponseEntity<Void> orderSingleProcessing(@RequestHeader(value = "X-User", required = false) Long userId,
                                                      @CookieValue(value = "X-GUEST") String guestId,
                                                      @Valid @RequestBody OrderGroupCreateRequestDTO orderGroupCreateRequestDTO) {
        if (userId != null) {
            orderProcessingStrategy.processMemberOrder(userId, orderGroupCreateRequestDTO);
        } else {
            orderProcessingStrategy.processNonMemberOrder(guestId, orderGroupCreateRequestDTO);
        }

        return ResponseEntity.ok().build();
    }

}
