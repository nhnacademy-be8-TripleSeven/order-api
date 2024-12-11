package com.example.orderapi.controller;

import com.example.orderapi.dto.pointpolicy.PointPolicyCreateRequest;
import com.example.orderapi.dto.pointpolicy.PointPolicyUpdateRequest;
import com.example.orderapi.entity.PointPolicy.PointPolicy;
import com.example.orderapi.exception.notfound.impl.PointPolicyNotFoundException;
import com.example.orderapi.service.PointPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pointpolicies")
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    @GetMapping
    public List<PointPolicy> getAllPointPolicies(){
        List<PointPolicy> pointPolicies = pointPolicyService.findAll();
        if(pointPolicies.isEmpty()){
            throw new PointPolicyNotFoundException("PointPolicies not found");
        }
        return pointPolicies;
    }

    @GetMapping("/{pointPolicyId}")
    public PointPolicy getPointPolicy(@PathVariable String pointPolicyId) {
        PointPolicy pointPolicy = pointPolicyService.findById(Long.parseLong(pointPolicyId));
        if(Objects.isNull(pointPolicy)){
            throw new PointPolicyNotFoundException("pointPolicyId:" + pointPolicyId + " not found");
        }
        return pointPolicy;
    }


    @PostMapping
    public ResponseEntity<PointPolicy> createPointPolicy(@RequestBody PointPolicyCreateRequest request) {
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.setName(request.getName());
        pointPolicy.setAmount(request.getAmount());
        pointPolicy.setRate(request.getRate());
        PointPolicy savedPointPolicy = pointPolicyService.save(pointPolicy);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPointPolicy);
    }


    @DeleteMapping("/{pointPolicyId}")
    public ResponseEntity<PointPolicy> deletePointPolicy(@PathVariable String pointPolicyId) {
        pointPolicyService.delete(Long.parseLong(pointPolicyId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("{pointPolicyId}")
    public ResponseEntity<PointPolicy> updatePointPolicy(@PathVariable String pointPolicyId, @RequestBody PointPolicyUpdateRequest request) {
        PointPolicy pointPolicy = pointPolicyService.findById(Long.parseLong(pointPolicyId));
        if(Objects.isNull(pointPolicy)){
            throw new PointPolicyNotFoundException("pointPolicyId:" + pointPolicyId + " not found");
        }
        pointPolicy.setName(request.getName());
        pointPolicy.setAmount(request.getAmount());
        pointPolicy.setRate(request.getRate());
        pointPolicyService.update(pointPolicy);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
