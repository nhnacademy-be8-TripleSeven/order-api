package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailUpdateRequestDTO;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "OrderDetail-Controller", description = "주문 상세 관리 컨트롤러")
@RestController
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    // 1. 주문 상세 단건 조회
    @GetMapping("/order-details/{id}")
    @Operation(summary = "주문 상세 단건 조회", description = "특정 주문 상세를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "주문 상세가 존재하지 않음")
    })
    public ResponseEntity<OrderDetailResponseDTO> getOrderDetailById(@PathVariable Long id) {
        OrderDetailResponseDTO response = orderDetailService.getOrderDetailService(id);
        return ResponseEntity.ok(response); // 반환: 주문 상세 정보 (OrderDetailResponse)
    }

    // 2. 주문 상세 생성
    @PostMapping("/order-details")
    @Operation(summary = "주문 상세 생성", description = "새로운 주문 상세를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<OrderDetailResponseDTO> createOrderDetail(@RequestBody OrderDetailCreateRequestDTO request) {
        OrderDetailResponseDTO response = orderDetailService.createOrderDetail(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 반환: 생성된 주문 상세 (OrderDetailResponse)
    }

    // 4. 주문 상세 삭제
    @DeleteMapping("/order-details/{id}")
    @Operation(summary = "주문 상세 삭제", description = "특정 주문 상세를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "주문 상세가 존재하지 않음")
    })
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable Long id) {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 반환: HTTP 204 (내용 없음)
    }

    // 5. 주문 그룹별 주문 상세 목록 조회
    @GetMapping("/order-details/order-groups/{orderGroupId}")
    @Operation(summary = "주문 그룹별 주문 상세 목록 조회", description = "특정 주문 그룹에 속한 주문 상세 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "주문 상세가 존재하지 않음")
    })
    public ResponseEntity<List<OrderDetailResponseDTO>> getOrderDetailsByOrderGroupId(@PathVariable Long orderGroupId) {
        List<OrderDetailResponseDTO> responses = orderDetailService.getOrderDetailsToList(orderGroupId);
        return ResponseEntity.ok(responses); // 반환: 주문 상세 목록 (List<OrderDetailResponse>)
    }

    @GetMapping("/order-details/check-purchase")
    public ResponseEntity<Boolean> checkUserPurchase(
            @RequestParam Long userId,
            @RequestParam Long bookId) {
        boolean hasPurchased = orderDetailService.hasUserPurchasedBook(userId, bookId);
        return ResponseEntity.ok(hasPurchased);
    }

    @PostMapping("/api/orders/order-details/status")
    @Operation(summary = "주문 상세 상태 업데이트(사용자)", description = "특정 주문 상세의 상태를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "주문 상세가 존재하지 않음")
    })
    public ResponseEntity<Void> updateOrderDetails(
            @RequestHeader("X-USER") Long userId,
            @RequestBody OrderDetailUpdateRequestDTO orderDetailUpdateRequestDTO) {
        orderDetailService.updateOrderDetailStatus(orderDetailUpdateRequestDTO.getOrderIds(), orderDetailUpdateRequestDTO.getOrderStatus());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/orders/order-details/status")
    @Operation(summary = "주문 상세 상태 업데이트(관리자)", description = "특정 주문 상세의 상태를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "주문 상세가 존재하지 않음")
    })
    public ResponseEntity<Void> updateOrderDetails(
            @RequestBody OrderDetailUpdateRequestDTO orderDetailUpdateRequestDTO) {
        orderDetailService.updateAdminOrderDetailStatus(orderDetailUpdateRequestDTO.getOrderIds(), orderDetailUpdateRequestDTO.getOrderStatus());
        return ResponseEntity.ok().build();
    }


}