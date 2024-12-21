package com.example.orderapi.controller;

import com.example.orderapi.dto.paytypes.PayTypeCreateRequest;
import com.example.orderapi.dto.paytypes.PayTypesResponse;
import com.example.orderapi.service.paytypes.PayTypesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "PayTypes-Controller", description = "결제 방식 컨트롤러")
@RequiredArgsConstructor
@RestController
public class PayTypesController {

    private final PayTypesService payTypesService;

    // 모든 결제 유형 조회
    @GetMapping("/pay-types")
    @Operation(summary = "모든 결제유형 조회", description = "결제 유형을 전부 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<List<PayTypesResponse>> getAllPayTypes() {
        List<PayTypesResponse> payTypes = payTypesService.getAllPayTypes();
        return ResponseEntity.ok(payTypes);
    }

    // 특정 결제 유형 조회 (ID로 조회)
    @GetMapping("/pay-types/{id}")
    @Operation(summary = "결제유형 단건 조회", description = "Id를 이용해 특정 결제 유형을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 결제 유형을 찾을 수 없음")
    })
    public ResponseEntity<PayTypesResponse> getPayTypeById(@PathVariable Long id) {
        PayTypesResponse payType = payTypesService.getPayTypeById(id);
        return ResponseEntity.ok(payType);
    }

    // 결제 유형 생성
    @PostMapping("/admin/pay-types")
    @Operation(summary = "결제 유형 생성", description = "새로운 결제 유형을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "결제 유형 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<PayTypesResponse> createPayType(@RequestBody PayTypeCreateRequest request) {
        PayTypesResponse createdPayType = payTypesService.createPayType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayType);
    }

    // 결제 유형 수정
    @PutMapping("/admin/pay-types/{id}")
    @Operation(summary = "결제 유형 수정", description = "결제 유형을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 유형 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "해당 결제 유형을 찾을 수 없음")
    })
    public ResponseEntity<PayTypesResponse> updatePayType(@PathVariable Long id, @RequestBody PayTypeCreateRequest request) {
        PayTypesResponse updatedPayType = payTypesService.updatePayType(id, request);
        return ResponseEntity.ok(updatedPayType);
    }

    // 결제 유형 삭제
    @DeleteMapping("/admin/pay-types/{id}")
    @Operation(summary = "결제 유형 삭제", description = "결제 유형을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "결제 유형 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 결제 유형을 찾을 수 없음")
    })
    public ResponseEntity<Void> removePayType(@PathVariable Long id) {
        payTypesService.removePayType(id);
        return ResponseEntity.noContent().build();
    }


}