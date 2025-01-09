package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;
import com.tripleseven.orderapi.service.defaultdeliverypolicy.DefaultDeliveryPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DefaultDeliveryController {
    private final DefaultDeliveryPolicyService defaultDeliveryPolicyService;

    // 정책 값 가져오기
    @GetMapping("/admin/orders/default-delivery")
    public ResponseEntity<DefaultDeliveryPolicyDTO> getDefaultPointPolicy() {
        DefaultDeliveryPolicyDTO defaultDelivery = defaultDeliveryPolicyService.getDefaultDeliveryDTO(DeliveryPolicyType.DEFAULT);

        return ResponseEntity.ok(defaultDelivery);
    }

    // 정책 수정
    @PutMapping("/admin/orders/default-delivery")
    public ResponseEntity<Long> updateDefaultPointPolicy(
            @RequestBody DefaultDeliveryPolicyUpdateRequestDTO request
    ) {
        Long defaultId = defaultDeliveryPolicyService.updateDefaultDelivery(request);
        return ResponseEntity.ok(defaultId);
    }
}
