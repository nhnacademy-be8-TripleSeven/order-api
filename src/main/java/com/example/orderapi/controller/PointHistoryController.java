package com.example.orderapi.controller;

import com.example.orderapi.entity.PointHistory.PointHistory;
import com.example.orderapi.exception.notfound.impl.PointHistoryNotFoundException;
import com.example.orderapi.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pointHistory")
public class PointHistoryController {

    private final PointHistoryService pointHistoryService;

    @GetMapping
    public Page<PointHistory> findAll(Pageable pageable) {
        Page<PointHistory>histories = pointHistoryService.findAll(pageable);
        if(histories.isEmpty())
            throw new PointHistoryNotFoundException("point history not found");

        return histories;
    }

    @GetMapping("/{memberId}")
    public Page<PointHistory> findByMemberId(@PathVariable Long memberId, Pageable pageable) {
        Page<PointHistory> history = pointHistoryService.findByMemberId(memberId, pageable);
        if(history.isEmpty()){
            throw new PointHistoryNotFoundException("memberId: " + memberId + "point history not found");
        }
        return history;
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<PointHistory> deletePointHistoryByMemberId(@PathVariable Long memberId) {
        pointHistoryService.deleteByMemberId(memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<PointHistory> deletePointHistoryByPointHistoryId(@RequestParam Long pointHistoryId) {
        if(Objects.isNull(pointHistoryService.findByPointHistoryId(pointHistoryId))){
            throw new PointHistoryNotFoundException("pointHistoryId: " + pointHistoryId + "point history not found");
        }
        pointHistoryService.deleteByPointHistoryId(pointHistoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
