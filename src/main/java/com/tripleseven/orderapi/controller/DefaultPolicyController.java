package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.policy.DefaultPolicyService;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.dto.defaultpolicy.DefaultPolicyDTO;
import com.tripleseven.orderapi.service.defaultdeliverypolicy.DefaultDeliveryPolicyService;
import com.tripleseven.orderapi.service.defaultpointpolicy.DefaultPointPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DefaultPolicyController {
    private final DefaultDeliveryPolicyService defaultDeliveryPolicyService;
    private final DefaultPointPolicyService defaultPointPolicyService;
    private final DefaultPolicyService defaultPolicyService;

    // 각 정책 별 값 가져오기
    @GetMapping("/admin/orders/default-policies")
    public ResponseEntity<DefaultPolicyDTO> getDefaultPointPolicies() {
        DefaultPolicyDTO defaultPolicy = defaultPolicyService.getDefaultPolicies();
        return ResponseEntity.ok(defaultPolicy);
    }

    // 정책 수정
    @PutMapping("/admin/orders/default-policy/point")
    public ResponseEntity<Long> updateDefaultPointPolicy(
            @RequestBody DefaultPointPolicyUpdateRequestDTO request
    ) {
        Long defaultId = defaultPointPolicyService.updateDefaultPoint(request);
        return ResponseEntity.ok(defaultId);
    }

    // 정책 수정
    @PutMapping("/admin/orders/default-policy/delivery")
    public ResponseEntity<Long> updateDefaultDeliveryPolicy(
            @RequestBody DefaultDeliveryPolicyUpdateRequestDTO request
    ) {
        Long defaultId = defaultDeliveryPolicyService.updateDefaultDelivery(request);
        return ResponseEntity.ok(defaultId);
    }
}
