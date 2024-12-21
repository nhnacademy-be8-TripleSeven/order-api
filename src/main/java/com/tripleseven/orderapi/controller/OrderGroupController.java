package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponse;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupUpdateDeliveryInfoRequest;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
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

@Tag(name = "OrderGroup-Controller", description = "주문 그룹 관리 컨트롤러")
@RestController
@RequiredArgsConstructor
public class OrderGroupController {

    private final OrderGroupService orderGroupService;

    // 1. 주문 그룹 단건 조회
    @GetMapping("/order-groups/{id}")
    @Operation(summary = "주문 그룹 단건 조회", description = "특정 ID의 주문 그룹 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "주문 그룹이 존재하지 않음")
    })
    public ResponseEntity<OrderGroupResponse> getOrderGroupById(@PathVariable Long id) {
        OrderGroupResponse response = orderGroupService.getOrderGroupById(id);
        return ResponseEntity.ok(response); // 반환: 주문 그룹 정보 (OrderGroupResponse)
    }

    // 2. 사용자별 주문 그룹 페이지 조회
    @GetMapping("/order-groups")
    @Operation(summary = "사용자별 주문 그룹 조회", description = "현재 사용자와 관련된 주문 그룹을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "주문 그룹이 존재하지 않음")
    })
    public ResponseEntity<Page<OrderGroupResponse>> getOrderGroupsByUserId(
            @RequestHeader("X-USER") Long userId, // 사용자 ID를 요청 헤더에서 가져옵니다.
            Pageable pageable) {
        Page<OrderGroupResponse> responses = orderGroupService.getOrderGroupPagesByUserId(userId, pageable);
        return ResponseEntity.ok(responses); // 반환: 주문 그룹 페이지 (Page<OrderGroupResponse>)
    }

    // 3. 주문 그룹 생성
    @PostMapping("/order-groups")
    @Operation(summary = "주문 그룹 생성", description = "새로운 주문 그룹을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<OrderGroupResponse> createOrderGroup(@RequestBody OrderGroupCreateRequest request) {
        OrderGroupResponse response = orderGroupService.createOrderGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 반환: 생성된 주문 그룹 (OrderGroupResponse)
    }

    // 4. 주문 그룹 배송 정보 업데이트
    @PutMapping("/order-groups/{id}/delivery-info")
    @Operation(summary = "주문 그룹 배송 정보 업데이트", description = "특정 주문 그룹의 배송 정보를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "주문 그룹이 존재하지 않음")
    })
    public ResponseEntity<OrderGroupResponse> updateOrderGroupDeliveryInfo(
            @PathVariable Long id,
            @RequestBody OrderGroupUpdateDeliveryInfoRequest request) {
        OrderGroupResponse response = orderGroupService.updateOrderGroup(id, request);
        return ResponseEntity.ok(response); // 반환: 업데이트된 주문 그룹 (OrderGroupResponse)
    }

    // 5. 주문 그룹 삭제
    @DeleteMapping("/order-groups/{id}")
    @Operation(summary = "주문 그룹 삭제", description = "특정 주문 그룹을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "주문 그룹이 존재하지 않음")
    })
    public ResponseEntity<Void> deleteOrderGroup(@PathVariable Long id) {
        orderGroupService.deleteOrderGroup(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 반환: HTTP 204 (내용 없음)
    }


    @GetMapping("/order-groups/period")
    @Operation(summary = "주문 그룹 기간별 조회", description = "특정 사용자에 대해 지정된 기간 내 주문 그룹을 조회합니다. " +
            "시작 날짜와 종료 날짜를 지정하지 않으면, 기본적으로 현재 날짜부터 내일 날짜까지의 범위로 조회됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "404", description = "주문 그룹을 찾을 수 없음")
    })
    public ResponseEntity<Page<OrderGroupResponse>> getOrderGroupPeriod(
            @RequestHeader("X-USER") Long memberId,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            Pageable pageable) {

        Page<OrderGroupResponse> orderGroupResponses = orderGroupService.getOrderGroupPeriodByUserId(memberId, startDate, endDate, pageable);
        return ResponseEntity.ok(orderGroupResponses);
    }
}