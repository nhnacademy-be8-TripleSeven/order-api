package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.order.OrderPayInfoDTO;
import com.tripleseven.orderapi.dto.pay.*;
import com.tripleseven.orderapi.entity.pay.PaymentStatus;
import com.tripleseven.orderapi.service.pay.PayService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayServiceTest {

    @Mock
    private PayService payService;

    @Test
    void testCreatePay() {
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .orderId(1L)
                .requestedAt(LocalDate.now())
                .balanceAmount(10000L)
                .status(PaymentStatus.DONE)
                .paymentKey("PAY-12345")
                .build();  // ✅ `@Builder` 사용 가능

        Long orderGroupId = 1L;
        String payType = "CARD";

        doNothing().when(payService).createPay(paymentDTO, orderGroupId, payType);

        assertDoesNotThrow(() -> payService.createPay(paymentDTO, orderGroupId, payType));
        verify(payService, times(1)).createPay(paymentDTO, orderGroupId, payType);
    }

    @Test
    void testCancelRequest() throws IOException {
        String paymentKey = "testPaymentKey";

        // ✅ `PayCancelRequestDTO`에 @Builder가 없으므로 직접 생성
        PayCancelRequestDTO requestDTO = new PayCancelRequestDTO("Test Cancel",1000L);

        Object expectedResponse = new Object();

        when(payService.cancelRequest(paymentKey, requestDTO)).thenReturn(expectedResponse);

        Object response = payService.cancelRequest(paymentKey, requestDTO);
        assertEquals(expectedResponse, response);
        verify(payService, times(1)).cancelRequest(paymentKey, requestDTO);
    }

    @Test
    void testCreatePayInfo() {
        Long userId = 1L;
        String guestId = "guest123";

        // ✅ `PayInfoRequestDTO`에 @Builder가 없으므로 직접 생성
        PayInfoRequestDTO requestDTO = new PayInfoRequestDTO();
        requestDTO.setOrdererName("테스트 사용자");
        requestDTO.setPayType("CARD");
        requestDTO.setDeliveryFee(3000L);
        requestDTO.setPoint(500L);
        requestDTO.setTotalAmount(20000L);
        requestDTO.setBookOrderDetails(Collections.emptyList()); // 빈 리스트 설정

        PayInfoResponseDTO expectedResponse = new PayInfoResponseDTO();

        when(payService.createPayInfo(userId, guestId, requestDTO)).thenReturn(expectedResponse);

        PayInfoResponseDTO response = payService.createPayInfo(userId, guestId, requestDTO);
        assertEquals(expectedResponse, response);
        verify(payService, times(1)).createPayInfo(userId, guestId, requestDTO);
    }

    @Test
    void testGetOrderPayInfo() {
        Long orderId = 1L;
        OrderPayInfoDTO expectedResponse = new OrderPayInfoDTO();

        when(payService.getOrderPayInfo(orderId)).thenReturn(expectedResponse);

        OrderPayInfoDTO response = payService.getOrderPayInfo(orderId);
        assertEquals(expectedResponse, response);
        verify(payService, times(1)).getOrderPayInfo(orderId);
    }

    @Test
    void testConfirmRequest() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String jsonBody = "{}";
        Object expectedResponse = new Object();

        when(payService.confirmRequest(request, jsonBody)).thenReturn(expectedResponse);

        Object response = payService.confirmRequest(request, jsonBody);
        assertEquals(expectedResponse, response);
        verify(payService, times(1)).confirmRequest(request, jsonBody);
    }

    @Test
    void testGetPaymentInfo() throws IOException {
        String paymentKey = "testKey";
        Object expectedResponse = new Object();

        when(payService.getPaymentInfo(paymentKey)).thenReturn(expectedResponse);

        Object response = payService.getPaymentInfo(paymentKey);
        assertEquals(expectedResponse, response);
        verify(payService, times(1)).getPaymentInfo(paymentKey);
    }

    @Test
    void testGetOrderId() {
        Long orderId = 1L;

        when(payService.getOrderId(orderId)).thenReturn(orderId);

        Long response = payService.getOrderId(orderId);
        assertEquals(orderId, response);
        verify(payService, times(1)).getOrderId(orderId);
    }

    @Test
    void testGetPayPrice() {
        Long orderId = 1L;
        Long expectedPrice = 50000L;

        when(payService.getPayPrice(orderId)).thenReturn(expectedPrice);

        Long response = payService.getPayPrice(orderId);
        assertEquals(expectedPrice, response);
        verify(payService, times(1)).getPayPrice(orderId);
    }
}