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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "PointHistory-Controller", description = "포인트 기록 컨트롤러")
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
    @Operation(summary = "회원의 포인트 기록 조회", description = "특정 회원의 포인트을 전부 조회합니다.")
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
    @Operation(summary = "포인트 기록 단건 조회", description = "특정 포인트 기록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PointHistoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당하는 포인트 기록 없음")
    })
    public ResponseEntity<PointHistoryResponse> findById(@PathVariable Long pointHistoryId) {
        PointHistoryResponse response = pointHistoryService.getPointHistory(pointHistoryId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/members/{memberId}")// 해당 회원의 포인트 내역 삭제
    @Operation(summary = "포인트 기록 삭제", description = "특정 회원의 포인트 기록을 전부 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 회원의 포인트 기록 없음")
    })
    public ResponseEntity<PointHistoryResponse> deletePointHistoryByMemberId(@PathVariable Long memberId) {
        pointHistoryService.removePointHistoriesByMemberId(memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping //해당 포인트 내역 삭제
    @Operation(summary = "포인트 기록 삭제", description = "특정 포인트 기록 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 포인트 기록 없음")
    })
    public ResponseEntity<PointHistoryResponse> deletePointHistoryByPointHistoryId(@RequestParam Long pointHistoryId) {
        pointHistoryService.removePointHistoryById(pointHistoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PostMapping //포인트 적립(결제로 인한 적립 제외)
    @Operation(summary = "포인트 기록 생성", description = "포인트 기록을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "생성 성공",
            content = @Content(schema = @Schema(implementation = PointHistoryResponse.class))),
            @ApiResponse(responseCode = "400",description = "잘못된 요청"),
            @ApiResponse(responseCode = "404",description = "해당하는 포인트 정책 없음(정책에 의해 기록이 생성됨)")
    })
    public ResponseEntity<PointHistoryResponse> createFromRequest(@RequestBody PointHistoryCreateRequest request) {
        PointHistoryResponse response = pointHistoryService.createPointHistory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/members/{memberId}/point")
    @Operation(summary = "포인트 잔액 조회", description = "특정회원의 포인트 잔액을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "조회 성공",
            content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "404",description = "해당하는 회원 아이디 없음")
    })
    public ResponseEntity<Integer> getPoint(@PathVariable Long memberId) {
        return ResponseEntity.ok(pointHistoryService.getTotalPointByMemberId(memberId));
    }

}
