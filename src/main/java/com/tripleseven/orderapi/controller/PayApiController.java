package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.order.process.OrderProcessing;
import com.tripleseven.orderapi.dto.pay.ErrorDTO;
import com.tripleseven.orderapi.dto.pay.PayCancelRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoResponseDTO;
import com.tripleseven.orderapi.dto.properties.ApiProperties;
import com.tripleseven.orderapi.service.pay.PayService;
import com.tripleseven.orderapi.service.pointhistory.PointHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Tag(name = "Payment API", description = "결제 관련 API를 제공합니다.")
public class PayApiController {

    private final ApiProperties apiProperties;
    private final Map<String, String> billingKeyMap = new HashMap<>();
    private final PayService payService;
    private final PointHistoryService pointHistoryService;
    private final OrderProcessing orderProcessing;

    @Operation(summary = "결제 페이지 정보 요청", description = "결제 페이지 요청에 필요한 정보를 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @PostMapping("/payments/order")
    public ResponseEntity<PayInfoResponseDTO> responseOrderInfo(
            @RequestHeader(value = "X-USER", required = false) Long userId,
            @CookieValue(value = "GUEST-ID") String guestId,
            @RequestBody PayInfoRequestDTO request) {

        PayInfoResponseDTO response = payService.createPayInfo(userId, guestId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 승인 요청", description = "Widget 또는 Payment 방식을 이용하여 결제를 승인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 승인 성공"),
            @ApiResponse(responseCode = "400", description = "결제 승인 실패")
    })
    @PostMapping(value = {"/confirm/widget", "/confirm/payment"})
    public ResponseEntity<?> confirmPayment(HttpServletRequest request,
                                                     @RequestHeader(value = "X-USER", required = false) Long userId,
                                                     @CookieValue(value = "GUEST-ID") String guestId,
                                                     @RequestBody String jsonBody) throws Exception {
        Object response = payService.confirmRequest(request,jsonBody);
        // TODO API 키 서비스에서 관리해서 DTO 만들어서
        //  서비스 로직으로 DTO 생성하여 후
        //  OrderProcessing 보내서 서비스 호출
        if(response.getClass().isAssignableFrom(ErrorDTO.class))
            return ResponseEntity.badRequest().body(response);

        if (userId != null) {
            orderProcessing.processMemberOrder(userId);
        } else {
            orderProcessing.processNonMemberOrder(guestId);
        }

        return ResponseEntity.ok(response);
    }

    //결제 취소 기능, 배송 이전엔 사용자가 주문 취소 가능, 배송 이후엔 반품 신청 등을 해야 주문 취소
    @Operation(summary = "결제 취소", description = "결제 키를 사용하여 결제를 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 취소 성공"),
            @ApiResponse(responseCode = "400", description = "결제 취소 실패")
    })
    @PostMapping("/payments/{paymentKey}/cancel")
    public ResponseEntity<?> cancelPayment(@PathVariable("paymentKey") String paymentKey, @RequestBody PayCancelRequestDTO request) throws Exception {
        Object response = payService.cancelRequest(paymentKey,request);

        if(response.getClass().isAssignableFrom(ErrorDTO.class))
            return ResponseEntity.badRequest().body(response);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 조회 (PaymentKey)", description = "PaymentKey를 사용하여 결제 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 조회 성공"),
            @ApiResponse(responseCode = "400", description = "결제 조회 실패")
    })
    @GetMapping("/payments/{paymentKey}")
    public ResponseEntity<?> getPayment(@PathVariable("paymentKey") String paymentKey) throws Exception {
        Object response = payService.getPaymentInfo(paymentKey);

        if(response.getClass().isAssignableFrom(ErrorDTO.class))
            return ResponseEntity.badRequest().body(response);

        return ResponseEntity.ok(response);
    }




}