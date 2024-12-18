package com.example.orderapi.controller;

import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequest;
import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyResponse;
import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequest;
import com.example.orderapi.service.deliverypolicy.DeliveryPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "DeliveryPolicy-Controller", description = "배송 정책 관리 컨트롤러")
@RequiredArgsConstructor
@RestController
public class DeliveryPolicyController {

    private final DeliveryPolicyService deliveryPolicyService;

    @GetMapping("/admin/delivery-policies/{id}")
    @Operation(summary = "배송 정책 조회", description = "특정 ID의 배송 정책을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "배송 정책이 존재하지 않음")
    })
    public ResponseEntity<DeliveryPolicyResponse> getDeliveryPolicy(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryPolicyService.getDeliveryPolicy(id));
    }

    @PostMapping("/admin/delivery-policies")
    @Operation(summary = "배송 정책 생성", description = "새로운 배송 정책을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<DeliveryPolicyResponse> createDeliveryPolicy(@RequestBody DeliveryPolicyCreateRequest request) {
        DeliveryPolicyResponse response = deliveryPolicyService.createDeliveryPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/admin/delivery-policies/{id}")
    @Operation(summary = "배송 정책 수정", description = "특정 배송 정책을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "배송 정책이 존재하지 않음")
    })
    public ResponseEntity<DeliveryPolicyResponse> updateDeliveryPolicy(
            @PathVariable Long id, @RequestBody DeliveryPolicyUpdateRequest request) {
        DeliveryPolicyResponse response = deliveryPolicyService.updateDeliveryPolicy(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/delivery-policies/{id}")
    @Operation(summary = "배송 정책 삭제", description = "특정 배송 정책을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "배송 정책이 존재하지 않음")
    })
    public ResponseEntity<Void> deleteDeliveryPolicy(@PathVariable Long id) {
        deliveryPolicyService.deleteDeliveryPolicy(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}