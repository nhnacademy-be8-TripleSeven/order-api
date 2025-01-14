package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyResponseDTO;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.service.deliverypolicy.DeliveryPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "DeliveryPolicy-Controller", description = "배송 정책 관리 컨트롤러")
@RequiredArgsConstructor
@RestController
public class DeliveryPolicyController {

    private final DeliveryPolicyService deliveryPolicyService;

    @GetMapping("/admin/orders/delivery-policies/{id}")
    @Operation(summary = "배송 정책 조회", description = "특정 ID의 배송 정책을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "배송 정책이 존재하지 않음")
    })
    public ResponseEntity<DeliveryPolicyResponseDTO> getDeliveryPolicy(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryPolicyService.getDeliveryPolicy(id));
    }

    @PostMapping("/admin/orders/delivery-policies")
    @Operation(summary = "배송 정책 생성", description = "새로운 배송 정책을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<DeliveryPolicyResponseDTO> createDeliveryPolicy(@RequestBody DeliveryPolicyCreateRequestDTO request) {
        DeliveryPolicyResponseDTO response = deliveryPolicyService.createDeliveryPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/admin/orders/delivery-policies/{id}")
    @Operation(summary = "배송 정책 수정", description = "특정 배송 정책을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "배송 정책이 존재하지 않음")
    })
    public ResponseEntity<DeliveryPolicyResponseDTO> updateDeliveryPolicy(
            @PathVariable Long id, @RequestBody DeliveryPolicyUpdateRequestDTO request) {
        DeliveryPolicyResponseDTO response = deliveryPolicyService.updateDeliveryPolicy(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/orders/delivery-policies/{id}")
    @Operation(summary = "배송 정책 삭제", description = "특정 배송 정책을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "배송 정책이 존재하지 않음")
    })
    public ResponseEntity<Void> deleteDeliveryPolicy(@PathVariable Long id) {
        deliveryPolicyService.deleteDeliveryPolicy(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/admin/orders/delivery-policies")
    @Operation(summary = "전체 배송 정책 조회", description = "모든 배송 정책을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "배송 정책이 존재하지 않음")
    })
    public ResponseEntity<List<DeliveryPolicyResponseDTO>> getAllDeliveryPolicies() {
        List<DeliveryPolicyResponseDTO> responses = deliveryPolicyService.getAllDeliveryPolicies();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}