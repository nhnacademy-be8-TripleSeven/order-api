package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.pay.*;
import com.tripleseven.orderapi.dto.properties.ApiProperties;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pay.Pay;
import com.tripleseven.orderapi.entity.pay.PaymentStatus;
import com.tripleseven.orderapi.entity.paytype.PayType;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.pay.PayRepository;
import com.tripleseven.orderapi.repository.paytypes.PayTypesRepository;
import com.tripleseven.orderapi.service.pay.PayServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayServiceTest {

    @Mock
    private ApiProperties apiProperties;

    @Mock
    private PayRepository payRepository;

    @Mock
    private OrderGroupRepository orderGroupRepository;

    @Mock
    private PayTypesRepository payTypesRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PayServiceImpl payService;

    private final Long orderGroupId = 1L;
    private final String payType = "CARD";
    private final String paymentKey = "test-payment-key";

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        payService = spy(payService); // Spy 적용
    }

    @Test
    void createPay_ShouldSavePay_WhenValidDataProvided() {
        // Given
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .orderId(100L)
                .requestedAt(LocalDate.now())
                .balanceAmount(50000L)
                .status(PaymentStatus.READY)
                .paymentKey(paymentKey)
                .build();

        OrderGroup orderGroup = new OrderGroup();
        PayType payTypeEntity = new PayType();
        when(orderGroupRepository.findById(orderGroupId)).thenReturn(Optional.of(orderGroup));
        when(payTypesRepository.findByName(payType)).thenReturn(payTypeEntity);

        // When
        payService.createPay(paymentDTO, orderGroupId, payType);

        // Then
        verify(payRepository, times(1)).save(any(Pay.class));
    }

    @Test
    void createPay_ShouldThrowException_WhenOrderGroupNotFound() {
        // Given
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .orderId(100L)
                .requestedAt(LocalDate.now())
                .balanceAmount(50000L)
                .status(PaymentStatus.READY)
                .paymentKey(paymentKey)
                .build();

        when(orderGroupRepository.findById(orderGroupId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> payService.createPay(paymentDTO, orderGroupId, payType));
    }

    @Test
    void createPayInfo_ShouldSavePayInfoToRedis() {
        // Given
        String guestId = "guest-123";
        Long userId = null;
        PayInfoRequestDTO requestDTO = mock(PayInfoRequestDTO.class); // ✅ Mock 객체 생성
        PayInfoResponseDTO mockResponse = new PayInfoResponseDTO(100L, 50000L); // ✅ 예상 반환 값 생성

        when(requestDTO.getTotalAmount()).thenReturn(50000L); // ✅ 필드 값 설정
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(payService.createPayInfo(userId, guestId, requestDTO)).thenReturn(mockResponse); // ✅ Mocking 처리

        // When
        PayInfoResponseDTO responseDTO = payService.createPayInfo(userId, guestId, requestDTO);

        // Then
        assertNotNull(responseDTO);
        verify(hashOperations, times(1)).put(eq(guestId), eq("PayInfo"), any(PayInfoDTO.class));
    }

    @Test
    void confirmRequest_ShouldReturnErrorDTO_WhenApiCallFails() throws IOException {
        // Given
        String jsonBody = "{}";

        when(apiProperties.getSecretApiKey()).thenReturn("secret-key");
        when(request.getRequestURI()).thenReturn("/confirm/payment"); // ✅ 추가된 부분

        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", true);
        errorResponse.put("code", "ERROR_CODE");
        errorResponse.put("message", "Payment failed");

        doReturn(errorResponse).when(payService).sendRequest(any(), anyString(), anyString());

        // When
        Object response = payService.confirmRequest(request, jsonBody);

        // Then
        assertTrue(response instanceof ErrorDTO);
    }

    @Test
    void getPaymentInfo_ShouldReturnPaymentDTO_WhenApiCallSucceeds() throws IOException {
        // Given
        when(apiProperties.getSecretApiKey()).thenReturn("secret-key");

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("orderId", 100L);
        jsonResponse.put("paymentKey", paymentKey);
        jsonResponse.put("balanceAmount", 50000L);
        jsonResponse.put("status", "DONE");
        jsonResponse.put("requestedAt", OffsetDateTime.now());

        doReturn(jsonResponse).when(payService).sendRequest(any(), anyString(), anyString());

        // When
        Object response = payService.getPaymentInfo(paymentKey);

        // Then
        assertNotNull(response);
        assertTrue(response instanceof PaymentDTO);
    }

    @Test
    void getOrderId_ShouldReturnOrderGroupId() {
        // Given
        Long orderId = 100L;
        Pay pay = mock(Pay.class);
        OrderGroup orderGroup = mock(OrderGroup.class);
        when(pay.getOrderGroup()).thenReturn(orderGroup);
        when(orderGroup.getId()).thenReturn(orderGroupId);
        when(payRepository.findByOrderId(orderId)).thenReturn(pay);

        // When
        Long result = payService.getOrderId(orderId);

        // Then
        assertEquals(orderGroupId, result);
    }

    @Test
    void getPayPrice_ShouldReturnPayPrice() {
        // Given
        Long orderId = 100L;
        Pay pay = mock(Pay.class);
        when(pay.getPrice()).thenReturn(50000L);
        when(payRepository.findByOrderId(orderId)).thenReturn(pay);

        // When
        Long result = payService.getPayPrice(orderId);

        // Then
        assertEquals(50000L, result);
    }

    @Test
    void sendRequest_ShouldReturnErrorResponse_WhenApiFails() throws IOException {
        // Given
        JSONObject requestData = new JSONObject();
        String secretKey = "test-secret-key";
        String url = "https://api.example.com/test";

        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", "Error reading response");

        doReturn(errorResponse).when(payService).sendRequest(any(), anyString(), anyString());

        // When
        JSONObject response = payService.sendRequest(requestData, secretKey, url);

        // Then
        assertTrue(response.containsKey("error"));
        assertEquals("Error reading response", response.get("error"));
    }

    @Test
    void sendRequest_ShouldReturnValidResponse_WhenApiCallSucceeds() throws IOException {
        // Given
        JSONObject requestData = new JSONObject();
        requestData.put("key", "value");

        String secretKey = "test-secret-key";
        String url = "https://api.example.com/test";

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", "SUCCESS");
        jsonResponse.put("data", "Sample response");

        // ✅ sendRequest가 호출될 때 정상 응답을 반환하도록 설정
        doReturn(jsonResponse).when(payService).sendRequest(any(), anyString(), anyString());

        // When
        JSONObject response = payService.sendRequest(requestData, secretKey, url);

        // Then
        assertNotNull(response);
        assertEquals("SUCCESS", response.get("status"));
        assertEquals("Sample response", response.get("data"));
    }

    @Test
    void sendRequest_ShouldThrowIOException_WhenResponseParsingFails() throws IOException {
        // Given
        JSONObject requestData = new JSONObject();
        String secretKey = "test-secret-key";
        String url = "https://api.example.com/test";

        // ✅ JSON 파싱 오류가 발생하도록 예외 설정
        doThrow(new IOException("응답 데이터를 파싱하는 도중 오류 발생"))
                .when(payService).sendRequest(any(), anyString(), anyString());

        // When & Then
        IOException exception = assertThrows(IOException.class, () -> payService.sendRequest(requestData, secretKey, url));
        assertTrue(exception.getMessage().contains("응답 데이터를 파싱하는 도중 오류 발생"));
    }

    @Test
    void sendRequest_ShouldThrowIOException_WhenApiCallFails() throws IOException {
        // Given
        JSONObject requestData = new JSONObject();
        String secretKey = "test-secret-key";
        String url = "https://api.example.com/test";

        // ✅ HTTP 요청 중 IOException이 발생하도록 설정
        doThrow(new IOException("HTTP 요청 중 오류 발생"))
                .when(payService).sendRequest(any(), anyString(), anyString());

        // When & Then
        IOException exception = assertThrows(IOException.class, () -> payService.sendRequest(requestData, secretKey, url));
        assertTrue(exception.getMessage().contains("HTTP 요청 중 오류 발생"));
    }

}