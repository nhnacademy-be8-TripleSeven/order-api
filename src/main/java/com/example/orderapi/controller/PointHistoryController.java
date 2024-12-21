package com.example.orderapi.controller;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
import com.example.orderapi.entity.pointhistory.HistoryTypes;
import com.example.orderapi.service.pointhistory.PointHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "PointHistory-Controller", description = "포인트 기록 컨트롤러")
@RequiredArgsConstructor
@RestController
public class PointHistoryController {

    private final PointHistoryService pointHistoryService;


    // 모든 회원의 포인트 내역 조회
    @GetMapping("/admin/point-histories")
    @Operation(summary = "모든 포인트 기록 조회", description = "모든 회원의 모든 포인트 기록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "포인트 기록이 없음")
    })
    public ResponseEntity<Page<PointHistoryResponse>> findPointHistories(Pageable pageable) {
        Page<PointHistoryResponse> histories = pointHistoryService.getPointHistories(pageable);
        return ResponseEntity.ok(histories); // HTTP 200
    }

    // 특정 회원의 포인트 내역 조회
    @GetMapping("/api/point-histories")
    @Operation(summary = "회원의 포인트 기록 조회", description = "특정 회원의 포인트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "포인트 기록을 찾을 수 없음")
    })
    public ResponseEntity<Page<PointHistoryResponse>> findByMemberId(@RequestHeader("X-USER") Long memberId, Pageable pageable) {
        Page<PointHistoryResponse> history = pointHistoryService.getPointHistoriesByMemberId(memberId, pageable);
        return ResponseEntity.ok(history); // HTTP 200
    }

    // 특정 포인트 기록 조회
    @GetMapping("/point-histories/{pointHistoryId}")
    @Operation(summary = "포인트 기록 단건 조회", description = "특정 포인트 기록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당하는 포인트 기록 없음")
    })
    public ResponseEntity<PointHistoryResponse> findById(@PathVariable Long pointHistoryId) {
        PointHistoryResponse response = pointHistoryService.getPointHistory(pointHistoryId);
        return ResponseEntity.ok(response);
    }

    // 특정 회원의 포인트 내역 삭제
    @DeleteMapping("/admin/point-histories")
    @Operation(summary = "포인트 기록 삭제", description = "특정 회원의 포인트 기록을 전부 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 회원의 포인트 기록 없음")
    })
    public ResponseEntity<Void> deletePointHistoryByMemberId(@RequestHeader("X-USER") Long memberId) {
        pointHistoryService.removePointHistoriesByMemberId(memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 특정 포인트 기록 삭제
    @DeleteMapping("/admin/point-histories/{id}")
    @Operation(summary = "포인트 기록 삭제", description = "특정 포인트 기록을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 포인트 기록 없음")
    })
    public ResponseEntity<Void> deletePointHistoryByPointHistoryId(@PathVariable Long id) {
        pointHistoryService.removePointHistoryById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 포인트 기록 생성
    @PostMapping("/api/point-histories")
    @Operation(summary = "포인트 기록 생성", description = "포인트 기록을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<PointHistoryResponse> createFromRequest(
            @RequestHeader("X-USER")Long memberId,
            @RequestBody PointHistoryCreateRequest request) {
        PointHistoryResponse response = pointHistoryService.createPointHistory(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 특정 회원의 포인트 잔액 조회
    @GetMapping("user/point-histories/point")
    @Operation(summary = "포인트 잔액 조회", description = "특정 회원의 포인트 잔액을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당하는 회원 아이디 없음")
    })
    public ResponseEntity<Integer> getPoint(@RequestHeader("X-USER") Long memberId) {
        Integer balance = pointHistoryService.getTotalPointByMemberId(memberId);
        return ResponseEntity.ok(balance);
    }
    @GetMapping("/user/point-histories/period")
    public ResponseEntity<Page<PointHistoryResponse>> getPointHistoriesWithinPeriod(
            @RequestHeader("X-USER") Long memberId,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "sortDirection", defaultValue = "DESC") String sortDirection,
            Pageable pageable) {


        Page<PointHistoryResponse> pointHistories = pointHistoryService.getPointHistoriesWithinPeriod(memberId, startDate, endDate,sortDirection, pageable);
        return ResponseEntity.ok(pointHistories);
    }
    @GetMapping("/point-histories/state")
    @Operation(summary = "회원의 포인트 기록 상태별 조회", description = "특정 회원의 포인트 기록을 상태별로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "포인트 기록을 찾을 수 없음")
    })
    public ResponseEntity<Page<PointHistoryResponse>> findByMemberIdAndState(
            @RequestHeader("X-USER") Long memberId,
            @RequestParam("state") HistoryTypes state,  // 상태를 enum으로 받음
            Pageable pageable) {
        Page<PointHistoryResponse> histories = pointHistoryService.getPointHistoriesWithState(memberId, state, pageable);
        return ResponseEntity.ok(histories); // HTTP 200
    }
}