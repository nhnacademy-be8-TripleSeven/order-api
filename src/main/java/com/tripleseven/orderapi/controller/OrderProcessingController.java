package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.pay.OrderProcessingStrategy;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderProcessingController {
    private final OrderProcessingStrategy orderProcessingStrategy;

//    @PostMapping("/single")
//    public ResponseEntity<Void> orderSingleProcessing(@RequestHeader("X-User") Long userId,
//                                                      @Valid @RequestBody OrderGroupCreateRequestDTO orderGroupCreateRequestDTO) {
//        orderProcessingStrategy.processSingleOrder(userId, orderGroupCreateRequestDTO);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/cart")
//    public ResponseEntity<Void> orderMultiProcessing(@RequestHeader("X-User") Long userId,
//                                                     @Valid @RequestBody OrderGroupCreateRequestDTO orderGroupCreateRequestDTO) {
//        orderProcessingStrategy.processMultipleOrder(userId, orderGroupCreateRequestDTO);
//        return ResponseEntity.ok().build();
//    }
}
