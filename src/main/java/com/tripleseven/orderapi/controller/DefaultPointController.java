package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.service.defaultpointpolicy.DefaultPointPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DefaultPointController {
    private final DefaultPointPolicyService defaultPointPolicyService;

    // 각 정책 별 값 가져오기
    @GetMapping("/admin/orders/default-point")
    public ResponseEntity<List<DefaultPointPolicyDTO>> getDefaultPointPolicies() {
        List<DefaultPointPolicyDTO> defaultPointPolicies = defaultPointPolicyService.getDefaultPointPolicies();

        return ResponseEntity.ok(defaultPointPolicies);
    }

    // 정책 수정
    @PutMapping("/admin/orders/default-point")
    public ResponseEntity<Long> updateDefaultPointPolicy(
            @RequestBody DefaultPointPolicyUpdateRequestDTO request
    ) {
        Long defaultId = defaultPointPolicyService.updateDefaultPoint(request);
        return ResponseEntity.ok(defaultId);
    }
}
