package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoArrivedAtUpdateRequest;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequest;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponse;
import com.tripleseven.orderapi.service.deliverycode.DeliveryCodeService;
import com.tripleseven.orderapi.service.deliveryinfo.DeliveryInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "DeliveryInfo-Controller", description = "배송 정보 관리 컨트롤러")
@RestController
@RequiredArgsConstructor
public class DeliveryInfoController {

    private final DeliveryInfoService deliveryInfoService;

    // 1. 특정 배송 정보 조회
    @GetMapping("/delivery-info/{id}")
    @Operation(summary = "배송 정보 조회", description = "특정 ID의 배송 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "배송 정보가 존재하지 않음")
    })
    public ResponseEntity<DeliveryInfoResponse> getDeliveryInfo(@PathVariable Long id) {
        DeliveryInfoResponse response = deliveryInfoService.getDeliveryInfoById(id);

        return ResponseEntity.ok(response); // 반환: 배송 정보 (DeliveryInfoResponse)
    }

    // 2. 배송 정보 생성
    @PostMapping("/delivery-info")
    @Operation(summary = "배송 정보 생성", description = "새로운 배송 정보를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<DeliveryInfoResponse> createDeliveryInfo(@RequestBody DeliveryInfoCreateRequest request) {
        DeliveryInfoResponse response = deliveryInfoService.createDeliveryInfo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 반환: 생성된 배송 정보 (DeliveryInfoResponse)
    }

    // 4. 배송 도착 시간 업데이트
    @PutMapping("/delivery-info/{id}/arrived-at")
    @Operation(summary = "배송 도착 시간 업데이트", description = "배송의 도착 시간을 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "배송 정보가 존재하지 않음")
    })
    public ResponseEntity<DeliveryInfoResponse> updateDeliveryArrivedAt(
            @PathVariable Long id, @RequestBody DeliveryInfoArrivedAtUpdateRequest request) {
        DeliveryInfoResponse response = deliveryInfoService.updateDeliveryInfoArrivedAt(id, request);
        return ResponseEntity.ok(response); // 반환: 업데이트된 배송 정보 (DeliveryInfoResponse)
    }

    // 5. 배송 정보 삭제
    @DeleteMapping("/admin/delivery-info/{id}")
    @Operation(summary = "배송 정보 삭제", description = "특정 배송 정보를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "배송 정보가 존재하지 않음")
    })
    public ResponseEntity<Void> deleteDeliveryInfo(@PathVariable Long id) {
        deliveryInfoService.deleteDeliveryInfo(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 반환: HTTP 204 (내용 없음)
    }
}