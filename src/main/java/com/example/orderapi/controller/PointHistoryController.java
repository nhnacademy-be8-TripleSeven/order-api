package com.example.orderapi.controller;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
import com.example.orderapi.entity.pointhistory.PointHistory;
import com.example.orderapi.exception.notfound.impl.PointHistoryNotFoundException;
import com.example.orderapi.service.pointhistory.PointHistoryService;
import com.example.orderapi.service.pointpolicy.PointPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/point-histories")
public class PointHistoryController {

    private final PointHistoryService pointHistoryService;
    private final PointPolicyService pointPolicyService;

    @GetMapping
    public ResponseEntity<Page<PointHistory>> findPointHistories(Pageable pageable) {
        Page<PointHistory> histories = pointHistoryService.getAllPointHistories(pageable);

        return ResponseEntity.ok(histories); // HTTP 200
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<Page<PointHistoryResponse>> findByMemberId(@PathVariable Long memberId, Pageable pageable) {
        Page<PointHistoryResponse> history = pointHistoryService.getMemberPointHistory(memberId, pageable);

        return ResponseEntity.ok(history); // HTTP 200
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<PointHistoryResponse> deletePointHistoryByMemberId(@PathVariable Long memberId) {
        pointHistoryService.removeAllPointHistoriesForMember(memberId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping
    public ResponseEntity<PointHistoryResponse> deletePointHistoryByPointHistoryId(@RequestParam Long pointHistoryId) {

        pointHistoryService.removePointHistoryById(pointHistoryId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping //포인트 사용
    public ResponseEntity<PointHistoryResponse> createFromRequest(@RequestBody PointHistoryCreateRequest request) {

        PointHistoryResponse response = pointHistoryService.createPointHistory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/members/{memberId}/policies/{policyId}") //포인트 적립
    public ResponseEntity<PointHistoryResponse> createFromPolicy(@PathVariable Long policyId, @PathVariable Long memberId) {
        PointHistoryResponse response = pointHistoryService.assignPointBasedOnPolicy(policyId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<Integer> getPoint(@PathVariable Long memberId) {
        return ResponseEntity.ok(pointHistoryService.calculateTotalPoints(memberId));
    }

}
