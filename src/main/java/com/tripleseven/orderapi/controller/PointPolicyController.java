package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyCreateRequestDTO;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyResponseDTO;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.service.pointpolicy.PointPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "PointPolicy-Controller", description = "포인트 정책 컨트롤러")
@Slf4j
@RestController
@RequiredArgsConstructor
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    @GetMapping("/admin/orders/point-policies")
    @Operation(summary = "모든 포인트 정책 조회", description = "모든 포인트 정책을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패")
    })
    public ResponseEntity<List<PointPolicyResponseDTO>> getAllPointPolicies() {
        List<PointPolicyResponseDTO> responses = pointPolicyService.findAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/admin/orders/point-policies/{point-policy-id}")
    @Operation(summary = "포인트 정책 단건 조회", description = "해당하는 포인트 정책을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당하는 포인트 정책 없음.")
    })
    public ResponseEntity<PointPolicyResponseDTO> getPointPolicy(
            @PathVariable("point-policy-id") String pointPolicyId) {
        PointPolicyResponseDTO response = pointPolicyService.findById(Long.parseLong(pointPolicyId));
        return ResponseEntity.ok(response);
    }


    @PostMapping("/admin/orders/point-policies")
    @Operation(summary = "포인트 정책 생성", description = "포인트 정책을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<PointPolicyResponseDTO> createPointPolicy(@RequestBody PointPolicyCreateRequestDTO request) {
        PointPolicyResponseDTO savedPointPolicy = pointPolicyService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPointPolicy);
    }

    @PutMapping("/admin/orders/point-policies/{point-policy-id}")
    @Operation(summary = "포인트 정책 수정", description = "해당 포인트 정책을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당하는 포인트 정책 없음")
    })
    public ResponseEntity<PointPolicyResponseDTO> updatePointPolicy(
            @PathVariable("point-policy-id") Long pointPolicyId,
            @RequestBody PointPolicyUpdateRequestDTO request) {

        PointPolicyResponseDTO response = pointPolicyService.update(pointPolicyId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/admin/orders/point-policies/{point-policy-id}")
    @Operation(summary = "포인트 정책 삭제", description = "해당 포인트 정책을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당하는 포인트 정책 없음")
    })
    public ResponseEntity<PointPolicyResponseDTO> deletePointPolicy(
            @PathVariable("point-policy-id") String pointPolicyId) {
        pointPolicyService.delete(Long.parseLong(pointPolicyId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
