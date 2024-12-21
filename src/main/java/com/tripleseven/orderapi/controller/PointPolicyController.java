package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyCreateRequest;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyResponse;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyUpdateRequest;
import com.tripleseven.orderapi.service.pointpolicy.PointPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/point-policies")
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    @GetMapping
    public List<PointPolicyResponse> getAllPointPolicies(){
        return pointPolicyService.findAll();
    }

    @GetMapping("/{pointPolicyId}")
    public PointPolicyResponse getPointPolicy(@PathVariable String pointPolicyId) {
        return pointPolicyService.findById(Long.parseLong(pointPolicyId));
    }


    @PostMapping
    public ResponseEntity<PointPolicyResponse> createPointPolicy(@RequestBody PointPolicyCreateRequest request) {
        PointPolicyResponse savedPointPolicy = pointPolicyService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPointPolicy);
    }


    @DeleteMapping("/{pointPolicyId}")
    public ResponseEntity<PointPolicyResponse> deletePointPolicy(@PathVariable String pointPolicyId) {
        pointPolicyService.delete(Long.parseLong(pointPolicyId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("{pointPolicyId}")
    public ResponseEntity<PointPolicyResponse> updatePointPolicy(@PathVariable Long pointPolicyId, @RequestBody PointPolicyUpdateRequest request) {

        pointPolicyService.update(pointPolicyId,request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
