package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.order.OrderService;
import com.tripleseven.orderapi.dto.order.OrderManageRequestDTO;
import com.tripleseven.orderapi.dto.order.OrderPayDetailDTO;
import com.tripleseven.orderapi.dto.order.OrderViewsResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.pay.PayService;
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

import java.util.List;

@Tag(name = "OrderGroup-Controller", description = "주문 그룹 관리 컨트롤러")
@RestController
@RequiredArgsConstructor
public class OrderGroupController {

    private final OrderGroupService orderGroupService;
    private final OrderService orderService;
    private final PayService payService;

    // 1. 주문 그룹 단건 조회
    @GetMapping("/orders/order-groups/{id}")
    @Operation(summary = "주문 그룹 단건 조회", description = "특정 ID의 주문 그룹 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "주문 그룹이 존재하지 않음")
    })
    public ResponseEntity<OrderGroupResponseDTO> getOrderGroupById(@PathVariable Long id) {
        Long orderGroupId = payService.getOrderId(id);
        OrderGroupResponseDTO response = orderGroupService.getOrderGroupById(orderGroupId);
        return ResponseEntity.ok(response); // 반환: 주문 그룹 정보 (OrderGroupResponse)
    }

    // 5. 주문 그룹 삭제
    @DeleteMapping("/orders/order-groups/{id}")
    @Operation(summary = "주문 그룹 삭제", description = "특정 주문 그룹을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "주문 그룹이 존재하지 않음")
    })
    public ResponseEntity<Void> deleteOrderGroup(@PathVariable Long id) {
        orderGroupService.deleteOrderGroup(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 반환: HTTP 204 (내용 없음)
    }


    @PostMapping("/api/orders/order-groups/period")
    @Operation(summary = "주문 그룹 기간별 조회", description = "특정 사용자에 대해 지정된 기간 내 주문 그룹을 조회합니다. " +
            "시작 날짜와 종료 날짜를 지정하지 않으면, 기본적으로 현재 날짜부터 내일 날짜까지의 범위로 조회됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "404", description = "주문 그룹을 찾을 수 없음")
    })
    public ResponseEntity<Page<OrderViewsResponseDTO>> getOrderGroupPeriod(
            @RequestBody OrderManageRequestDTO manageRequestDTO,
            @RequestHeader("X-USER") Long userId,
            Pageable pageable) {

        Page<OrderViewsResponseDTO> orderViewsRequestDTOList = orderGroupService.getOrderGroupPeriodByUserId(userId, manageRequestDTO, pageable);
        return ResponseEntity.ok(orderViewsRequestDTOList);
    }

    @GetMapping("/api/orders/order-groups/{orderGroupId}")
    public ResponseEntity<OrderPayDetailDTO> getOrderGroupDetail(
            @RequestHeader("X-USER") Long userId,
            @PathVariable("orderGroupId") Long orderGroupId
    ) {
        OrderPayDetailDTO orderPayDetailDTO = orderService.getOrderPayDetail(userId, orderGroupId);
        return ResponseEntity.ok(orderPayDetailDTO);
    }

    @PostMapping("/admin/orders/order-groups/period")
    public ResponseEntity<Page<OrderViewsResponseDTO>> getAdminOrderGroupPeriod(
            @RequestBody OrderManageRequestDTO manageRequestDTO,
            Pageable pageable) {

        Page<OrderViewsResponseDTO> orderViewsRequestDTOList = orderGroupService.getOrderGroupPeriod(manageRequestDTO, pageable);
        return ResponseEntity.ok(orderViewsRequestDTOList);
    }

    @GetMapping("/admin/orders/order-groups/{orderGroupId}")
    public ResponseEntity<OrderPayDetailDTO> getAdminOrderGroupDetail(
            @PathVariable("orderGroupId") Long orderGroupId
    ) {
        OrderPayDetailDTO orderPayDetailDTO = orderService.getOrderPayDetailAdmin(orderGroupId);
        return ResponseEntity.ok(orderPayDetailDTO);
    }

    @GetMapping("/orders/order-groups")
    public List<OrderGroupResponseDTO> getGuestOrderGroups(@RequestParam String phone) {
        return orderGroupService.getGuestOrderGroups(phone);
    }
}