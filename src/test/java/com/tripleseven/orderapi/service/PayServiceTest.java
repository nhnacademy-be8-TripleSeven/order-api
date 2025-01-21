package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.order.OrderPayInfoDTO;
import com.tripleseven.orderapi.dto.pay.ErrorDTO;
import com.tripleseven.orderapi.dto.pay.PaymentDTO;
import com.tripleseven.orderapi.dto.pay.PayCancelRequestDTO;
import com.tripleseven.orderapi.dto.properties.ApiProperties;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pay.Pay;
import com.tripleseven.orderapi.entity.pay.PaymentStatus;
import com.tripleseven.orderapi.entity.paytype.PayType;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.pay.PayRepository;
import com.tripleseven.orderapi.repository.paytypes.PayTypesRepository;
import com.tripleseven.orderapi.service.pay.PayServiceImpl;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.HashOperations;

import java.io.IOException;
import java.time.LocalDate;
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
    void cancelRequest_ShouldReturnErrorDTO_WhenApiCallFails() throws IOException {
        // Given
        PayCancelRequestDTO requestDTO = new PayCancelRequestDTO();
        when(apiProperties.getSecretApiKey()).thenReturn("secret-key");

        // ✅ 예상 응답(JSON)에 필요한 값 추가
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", true);
        errorResponse.put("errorMessage", "Payment processing failed");

        doReturn(errorResponse).when(payService).sendRequest(any(), anyString(), anyString());

        // When
        Object response = payService.cancelRequest(paymentKey, requestDTO);

        // Then
        assertTrue(response instanceof ErrorDTO);
    }

    @Test
    void cancelRequest_ShouldUpdatePay_WhenApiCallSucceeds() throws IOException {
        // Given
        Pay pay = mock(Pay.class);
        when(payRepository.findByPaymentKey(paymentKey)).thenReturn(pay);
        when(apiProperties.getSecretApiKey()).thenReturn("secret-key");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("orderId", "100");
        jsonResponse.put("requestedAt", "2024-01-01");
        jsonResponse.put("balanceAmount", "50000");
        jsonResponse.put("status", "DONE");
        jsonResponse.put("paymentKey", paymentKey);
        doReturn(jsonResponse).when(payService).sendRequest(any(), anyString(), anyString());

        // When
        Object response = payService.cancelRequest(paymentKey, new PayCancelRequestDTO());

        // Then
        assertTrue(response instanceof PaymentDTO);
        verify(pay, times(1)).ofUpdate(any(PaymentDTO.class));
    }

    @Test
    void getOrderPayInfo_ShouldReturnDTO_WhenValidOrderIdProvided() {
        // Given
        Long orderId = 1L;
        when(payRepository.getDTOByOrderGroupId(orderId)).thenReturn(mock(OrderPayInfoDTO.class));
        // When
        Object result = payService.getOrderPayInfo(orderId);

        // Then
        assertNotNull(result);
    }
}
