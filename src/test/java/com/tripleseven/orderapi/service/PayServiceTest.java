package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import com.tripleseven.orderapi.dto.pay.*;
import com.tripleseven.orderapi.dto.properties.ApiProperties;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pay.Pay;
import com.tripleseven.orderapi.entity.pay.PaymentStatus;
import com.tripleseven.orderapi.entity.paytype.PayType;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.pay.PayRepository;
import com.tripleseven.orderapi.repository.paytypes.PayTypesRepository;
import com.tripleseven.orderapi.service.pay.PayServiceImpl;
import com.tripleseven.orderapi.client.BookCouponApiClient;
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
import java.util.List;
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

    @Mock
    private BookCouponApiClient bookCouponApiClient;

    @InjectMocks
    private PayServiceImpl payService;

    private final Long orderGroupId = 1L;
    private final String payType = "CARD";
    private final String paymentKey = "test-payment-key";

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        payService = spy(payService);
    }

    @Test
    void createPay_ShouldSavePay_WhenValidDataProvided() {
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

        payService.createPay(paymentDTO, orderGroupId, payType);

        verify(payRepository, times(1)).save(any(Pay.class));
    }

    @Test
    void createPay_ShouldThrowException_WhenOrderGroupNotFound() {
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .orderId(100L)
                .requestedAt(LocalDate.now())
                .balanceAmount(50000L)
                .status(PaymentStatus.READY)
                .paymentKey(paymentKey)
                .build();

        when(orderGroupRepository.findById(orderGroupId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> payService.createPay(paymentDTO, orderGroupId, payType));
    }

    @Test
    void createPayInfo_ShouldSavePayInfoToRedis() {
        String guestId = "guest-123";
        Long userId = null;
        PayInfoRequestDTO requestDTO = mock(PayInfoRequestDTO.class);
        PayInfoResponseDTO mockResponse = new PayInfoResponseDTO(100L, 50000L);

        when(requestDTO.getTotalAmount()).thenReturn(50000L);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(payService.createPayInfo(userId, guestId, requestDTO)).thenReturn(mockResponse);

        PayInfoResponseDTO responseDTO = payService.createPayInfo(userId, guestId, requestDTO);

        assertNotNull(responseDTO);
        verify(hashOperations, times(1)).put(eq(guestId), eq("PayInfo"), any(PayInfoDTO.class));
    }

    @Test
    void createPayInfo_ShouldThrowException_WhenCouponIsInvalid() {
        Long userId = 1L;
        String guestId = "guest-123";

        PayInfoRequestDTO requestDTO = mock(PayInfoRequestDTO.class);
        OrderBookInfoDTO bookInfo = mock(OrderBookInfoDTO.class);

        when(requestDTO.getBookOrderDetails()).thenReturn(List.of(bookInfo));
        when(requestDTO.getTotalAmount()).thenReturn(50000L);
        when(bookInfo.getCouponId()).thenReturn(1L);
        when(bookInfo.getPrice()).thenReturn(20000L);
        when(bookInfo.getCouponSalePrice()).thenReturn(5000L);
        when(bookCouponApiClient.applyCoupon(1L, 20000L)).thenReturn(4000L);

        assertThrows(CustomException.class,
                () -> payService.createPayInfo(userId, guestId, requestDTO),
                "쿠폰 할인 금액이 일치하지 않으면 예외 발생해야 함");
    }


    @Test
    void sendRequest_ShouldThrowIOException_WhenResponseParsingFails() throws IOException {
        JSONObject requestData = new JSONObject();
        String secretKey = "test-secret-key";
        String url = "https://api.example.com/test";

        doThrow(new IOException("응답 데이터를 파싱하는 도중 오류 발생"))
                .when(payService).sendRequest(any(), anyString(), anyString());

        IOException exception = assertThrows(IOException.class, () -> payService.sendRequest(requestData, secretKey, url));
        assertTrue(exception.getMessage().contains("응답 데이터를 파싱하는 도중 오류 발생"));
    }

    @Test
    void sendRequest_ShouldReturnValidResponse_WhenApiCallSucceeds() throws IOException {
        JSONObject requestData = new JSONObject();
        requestData.put("key", "value");

        String secretKey = "test-secret-key";
        String url = "https://api.example.com/test";

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", "SUCCESS");
        jsonResponse.put("data", "Sample response");

        doReturn(jsonResponse).when(payService).sendRequest(any(), anyString(), anyString());

        JSONObject response = payService.sendRequest(requestData, secretKey, url);

        assertNotNull(response);
        assertEquals("SUCCESS", response.get("status"));
        assertEquals("Sample response", response.get("data"));
    }

    @Test
    void sendRequest_ShouldThrowIOException_WhenApiCallFails() throws IOException {
        JSONObject requestData = new JSONObject();
        String secretKey = "test-secret-key";
        String url = "https://api.example.com/test";

        doThrow(new IOException("HTTP 요청 중 오류 발생"))
                .when(payService).sendRequest(any(), anyString(), anyString());

        IOException exception = assertThrows(IOException.class, () -> payService.sendRequest(requestData, secretKey, url));
        assertTrue(exception.getMessage().contains("HTTP 요청 중 오류 발생"));
    }



    @Test
    void cancelRequest_ShouldReturnErrorDTO_WhenApiCallFails() throws IOException {
        // Given
        String paymentKey = "test-payment-key";
        PayCancelRequestDTO requestDTO = new PayCancelRequestDTO("customer-request", 50000L);
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", true);
        errorResponse.put("code", "ERROR_CODE");
        errorResponse.put("message", "Payment cancellation failed");

        when(apiProperties.getSecretApiKey()).thenReturn("secret-key");
        doReturn(errorResponse).when(payService).sendRequest(any(), anyString(), eq(url));

        // When
        Object response = payService.cancelRequest(paymentKey, requestDTO);

        // Then
        assertNotNull(response);
        assertTrue(response instanceof ErrorDTO);
    }

    @Test
    void cancelRequest_ShouldThrowException_WhenPayNotFound() throws IOException {
        // Given
        String paymentKey = "test-payment-key";
        PayCancelRequestDTO requestDTO = new PayCancelRequestDTO("customer-request", 50000L);
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("orderId", 100L);
        jsonResponse.put("paymentKey", paymentKey);
        jsonResponse.put("balanceAmount", 50000L);
        jsonResponse.put("status", "CANCELED");

        when(apiProperties.getSecretApiKey()).thenReturn("secret-key");
        when(payRepository.findByPaymentKey(paymentKey)).thenReturn(null);
        doReturn(jsonResponse).when(payService).sendRequest(any(), anyString(), eq(url));

        // When & Then
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> payService.cancelRequest(paymentKey, requestDTO));
        assertTrue(exception.getMessage().contains("Cannot invoke"));
    }
}