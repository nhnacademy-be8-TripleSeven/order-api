package com.example.orderapi.controller;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
import com.example.orderapi.entity.pointhistory.PointHistory;
import com.example.orderapi.service.pointhistory.PointHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/point-histories")
public class PointHistoryController {

    private final PointHistoryService pointHistoryService;


    @GetMapping//모든 회원의  포인트 내역 조회
    @Operation(summary = "모든 포인트 기록 조회", description = "모든 회원의 모든 포인트 기록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PointHistoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "포인트 기록이 없음")
    })
    public ResponseEntity<Page<PointHistoryResponse>> findPointHistories(Pageable pageable) {
        Page<PointHistoryResponse> histories = pointHistoryService.getPointHistories(pageable);

        return ResponseEntity.ok(histories); // HTTP 200
    }

    @GetMapping("/members/{memberId}")//해당 회원의 포인트 내역 조회
    @Operation(summary = "회원의 포인트 기록 조회", description = "특정 회원의 포인트 기록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PointHistoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "포인트 기록을 찾을 수 없음")
    })
    public ResponseEntity<Page<PointHistoryResponse>> findByMemberId(@PathVariable Long memberId, Pageable pageable) {
        Page<PointHistoryResponse> history = pointHistoryService.getPointHistoriesByMemberId(memberId, pageable);

        return ResponseEntity.ok(history); // HTTP 200
    }

    @GetMapping("/{pointHistoryId}")
    public ResponseEntity<PointHistoryResponse> findById(@PathVariable Long pointHistoryId) {
        PointHistoryResponse response = pointHistoryService.getPointHistory(pointHistoryId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/members/{memberId}")// 해당 회원의 포인트 내역 삭제
    public ResponseEntity<PointHistoryResponse> deletePointHistoryByMemberId(@PathVariable Long memberId) {
        pointHistoryService.removePointHistoriesByMemberId(memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping //해당 포인트 내역 삭제
    public ResponseEntity<PointHistoryResponse> deletePointHistoryByPointHistoryId(@RequestParam Long pointHistoryId) {

        pointHistoryService.removePointHistoryById(pointHistoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PostMapping //포인트 적립(결제로 인한 적립 제외)
    public ResponseEntity<PointHistoryResponse> createFromRequest(@RequestBody PointHistoryCreateRequest request) {

        PointHistoryResponse response = pointHistoryService.createPointHistory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/members/{memberId}/point")
    public ResponseEntity<Integer> getPoint(@PathVariable Long memberId) {
        return ResponseEntity.ok(pointHistoryService.getTotalPointByMemberId(memberId));
    }

}
