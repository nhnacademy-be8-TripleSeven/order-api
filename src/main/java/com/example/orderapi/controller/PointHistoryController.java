package com.example.orderapi.controller;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
import com.example.orderapi.entity.PointHistory.PointHistory;
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
    public Page<PointHistory> findAll(Pageable pageable) {
        Page<PointHistory>histories = pointHistoryService.findAll(pageable);
        if(histories.isEmpty())
            throw new PointHistoryNotFoundException("point history not found");

        return histories;
    }

    @GetMapping("/{memberId}")
    public Page<PointHistoryResponse> findByMemberId(@PathVariable Long memberId, Pageable pageable) {
        Page<PointHistoryResponse> history = pointHistoryService.findByMemberId(memberId, pageable);
        if(history.isEmpty()){
            throw new PointHistoryNotFoundException("memberId: " + memberId + "point history not found");
        }
        return history;
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<PointHistoryResponse> deletePointHistoryByMemberId(@PathVariable Long memberId) {
        pointHistoryService.deleteByMemberId(memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping
    public ResponseEntity<PointHistoryResponse> deletePointHistoryByPointHistoryId(@RequestParam Long pointHistoryId) {
        if(Objects.isNull(pointHistoryService.findByPointHistoryId(pointHistoryId))){
            throw new PointHistoryNotFoundException("pointHistoryId: " + pointHistoryId + "point history not found");
        }
        pointHistoryService.deleteByPointHistoryId(pointHistoryId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping //포인트 사용
    public ResponseEntity<PointHistoryResponse> createFromRequest(@RequestBody PointHistoryCreateRequest request) {

        PointHistoryResponse response = pointHistoryService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("members/{memberId}/policies/{policyId}") //포인트 적립
    public ResponseEntity<PointHistoryResponse> createFromPolicy(@PathVariable Long policyId, @PathVariable Long memberId) {
        PointHistoryResponse response = pointHistoryService.save(policyId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/members/{memberId}")
    public Integer getPoint(@PathVariable Long memberId) {
        return pointHistoryService.getPoint(memberId);
    }

}
