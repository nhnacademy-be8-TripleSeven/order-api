package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.policy.DefaultPolicyService;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.dto.defaultpolicy.DefaultPolicyDTO;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;
import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import com.tripleseven.orderapi.service.defaultdeliverypolicy.DefaultDeliveryPolicyService;
import com.tripleseven.orderapi.service.defaultpointpolicy.DefaultPointPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DefaultPolicyController {
    private final DefaultDeliveryPolicyService defaultDeliveryPolicyService;
    private final DefaultPointPolicyService defaultPointPolicyService;
    private final DefaultPolicyService defaultPolicyService;

    // 모든 정책 별 모든 값 가져오기
    @GetMapping("/admin/orders/default-policies")
    public ResponseEntity<DefaultPolicyDTO> getDefaultPointPolicies() {
        DefaultPolicyDTO defaultPolicy = defaultPolicyService.getDefaultPolicies();
        return ResponseEntity.ok(defaultPolicy);
    }

    // 포인트 정책 수정
    @PutMapping("/admin/orders/default-policy/point")
    public ResponseEntity<Long> updateDefaultPointPolicy(
            @RequestBody DefaultPointPolicyUpdateRequestDTO request
    ) {
        Long defaultId = defaultPointPolicyService.updateDefaultPoint(request);
        return ResponseEntity.ok(defaultId);
    }

    // 배송 정책 수정
    @PutMapping("/admin/orders/default-policy/delivery")
    public ResponseEntity<Long> updateDefaultDeliveryPolicy(
            @RequestBody DefaultDeliveryPolicyUpdateRequestDTO request
    ) {
        Long defaultId = defaultDeliveryPolicyService.updateDefaultDelivery(request);
        return ResponseEntity.ok(defaultId);
    }

    // 기본 포인트 정책 가져오기
    @GetMapping("/orders/default-policy/point")
    public ResponseEntity<DefaultPointPolicyDTO> getDefaultPointPolicy(@RequestParam PointPolicyType type) {
        DefaultPointPolicyDTO dto = defaultPointPolicyService.getDefaultPointPolicy(type);
        return ResponseEntity.ok(dto);
    }

    // 기본 배송비 정책 가져오기
    @GetMapping("/orders/default-policy/delivery")
    public ResponseEntity<DefaultDeliveryPolicyDTO> getDefaultPointPolicy(@RequestParam DeliveryPolicyType type) {
        DefaultDeliveryPolicyDTO dto = defaultDeliveryPolicyService.getDefaultDeliveryPolicy(type);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/orders/default-policy/register")
    public ResponseEntity<DefaultPointPolicyDTO> createRegisterPointHistory(
            @RequestHeader("X-USER") Long userId
    ) {
        DefaultPointPolicyDTO dto = defaultPointPolicyService.createRegisterPointHistory(userId);
        return ResponseEntity.ok(dto);
    }

}
