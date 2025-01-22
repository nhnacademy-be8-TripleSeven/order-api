package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.order.process.OrderProcessing;
import com.tripleseven.orderapi.dto.pay.*;
import com.tripleseven.orderapi.dto.properties.ApiProperties;
import com.tripleseven.orderapi.entity.pay.PaymentStatus;
import com.tripleseven.orderapi.service.pay.PayService;
import com.tripleseven.orderapi.service.pointhistory.PointHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PayApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PayService payService;

    @Mock
    private ApiProperties apiProperties;

    @Mock
    private PointHistoryService pointHistoryService;

    @Mock
    private OrderProcessing orderProcessing;

    @InjectMocks
    private PayApiController payApiController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(payApiController).build();
    }

    @Test
    void testResponseOrderInfo() throws Exception {
        Long userId = 1L;
        String guestId = "guest123";
        PayInfoRequestDTO requestDTO = new PayInfoRequestDTO(); // 새로운 객체 생성
        PayInfoResponseDTO responseDTO = new PayInfoResponseDTO();

        doReturn(responseDTO)
                .when(payService)
                .createPayInfo(eq(userId), eq(guestId), any(PayInfoRequestDTO.class)); // any() 사용

        mockMvc.perform(post("/payments/order")
                        .header("X-USER", userId)
                        .cookie(new jakarta.servlet.http.Cookie("GUEST-ID", guestId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(payService, times(1))
                .createPayInfo(eq(userId), eq(guestId), any(PayInfoRequestDTO.class)); // 검증
    }

    @Test
    void testConfirmPayment() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String jsonBody = "{}";
        Long userId = 1L;
        String guestId = "guest123";
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .orderId(1L)
                .balanceAmount(10000L)
                .status(com.tripleseven.orderapi.entity.pay.PaymentStatus.DONE)
                .paymentKey("PAY-12345")
                .requestedAt(java.time.LocalDate.now())
                .build();

        when(payService.confirmRequest(any(), any())).thenReturn(paymentDTO);

        mockMvc.perform(post("/confirm/payment")
                        .header("X-USER", userId)
                        .cookie(new jakarta.servlet.http.Cookie("GUEST-ID", guestId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());

        verify(payService, times(1)).confirmRequest(any(), any());
        verify(orderProcessing, times(1)).processOrder(userId, guestId, paymentDTO);
    }

    @Test
    void testCancelPayment() throws Exception {
        // ✅ 테스트용 결제 키
        String paymentKey = "testPaymentKey";

        // ✅ 정상 응답을 가정한 PaymentDTO 생성
        PaymentDTO paymentResponse = PaymentDTO.builder()
                .orderId(1L)
                .requestedAt(LocalDate.now())
                .balanceAmount(5000L)
                .status(PaymentStatus.CANCELED)  // ✅ 결제 취소 상태로 설정
                .paymentKey(paymentKey)
                .build();

        // ✅ 요청 DTO 생성
        PayCancelRequestDTO requestDTO = new PayCancelRequestDTO("User request", 5000L);

        // ✅ payService.cancelRequest()가 호출되면 paymentResponse를 반환하도록 설정
        when(payService.cancelRequest(eq(paymentKey), any(PayCancelRequestDTO.class)))
                .thenReturn(paymentResponse);

        // ✅ JSON 요청 본문
        String requestBody = """
                {
                    "cancelReason": "User request",
                    "cancelAmount": 5000
                }
                """;

        mockMvc.perform(post("/payments/{paymentKey}/cancel", paymentKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())  // ✅ 200 응답 검증
                .andExpect(jsonPath("$.orderId").value(1L))  // ✅ 응답값 검증
                .andExpect(jsonPath("$.balanceAmount").value(5000))
                .andExpect(jsonPath("$.status").value("CANCELED"))
                .andExpect(jsonPath("$.paymentKey").value(paymentKey));

        // ✅ `cancelRequest()`가 정확히 1회 호출되었는지 검증
        verify(payService, times(1)).cancelRequest(eq(paymentKey), any(PayCancelRequestDTO.class));
    }

    @Test
    void testGetPayment_Success() throws Exception {
        String paymentKey = "testPaymentKey";

        // ✅ 성공 응답을 가정한 PaymentDTO 객체 생성
        PaymentDTO response = PaymentDTO.builder()
                .orderId(1L)
                .requestedAt(LocalDate.now())
                .balanceAmount(5000L)
                .status(PaymentStatus.DONE)
                .paymentKey(paymentKey)
                .build();

        // ✅ Mock 객체가 호출될 때 위의 객체를 반환하도록 설정
        when(payService.getPaymentInfo(paymentKey)).thenReturn(response);

        mockMvc.perform(get("/payments/{paymentKey}", paymentKey)
                        .accept(MediaType.APPLICATION_JSON))  // ✅ JSON 응답을 요청
                .andExpect(status().isOk());

        verify(payService, times(1)).getPaymentInfo(paymentKey);
    }

    @Test
    void testGetPayment_Error() throws Exception {
        String paymentKey = "invalidKey";

        // ✅ 실패 응답을 가정한 ErrorDTO 객체 생성
        ErrorDTO errorResponse = new ErrorDTO("ERROR_CODE", "Invalid Payment Key");

        // ✅ Mock 객체가 호출될 때 에러 응답을 반환하도록 설정
        when(payService.getPaymentInfo(paymentKey)).thenReturn(errorResponse);

        mockMvc.perform(get("/payments/{paymentKey}", paymentKey)
                        .accept(MediaType.APPLICATION_JSON))  // ✅ JSON 응답을 요청
                .andExpect(status().isBadRequest());  // ✅ 에러 발생 시 400 상태 반환

        verify(payService, times(1)).getPaymentInfo(paymentKey);
    }
}