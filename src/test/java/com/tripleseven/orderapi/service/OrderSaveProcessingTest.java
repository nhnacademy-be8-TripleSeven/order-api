package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.business.order.OrderService;
import com.tripleseven.orderapi.business.order.process.OrderSaveProcessing;
import com.tripleseven.orderapi.business.rabbit.RabbitService;
import com.tripleseven.orderapi.dto.order.AddressInfoDTO;
import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import com.tripleseven.orderapi.dto.order.RecipientInfoDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.PaymentDTO;
import com.tripleseven.orderapi.entity.pay.PaymentStatus;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderSaveProcessingTest {

    @Mock
    private RabbitService rabbitService;

    @Mock
    private HashOperations<String, String, PayInfoDTO> hashOperations;

    @Mock
    private RedisTemplate<String, PayInfoDTO> redisTemplate;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderSaveProcessing orderSaveProcessing;

    private PayInfoDTO payInfo;
    private PaymentDTO paymentDTO;

    @BeforeEach
    void setUp() {
        OrderBookInfoDTO orderBookInfoDTO =
                new OrderBookInfoDTO(
                        1L,
                        "title",
                        10000,
                        2,
                        1L,
                        1000
                );

        RecipientInfoDTO recipientInfoDTO = new RecipientInfoDTO("name", "01012345678", "01012345678");
        AddressInfoDTO addressInfoDTO = new AddressInfoDTO("road", "zone", "detail");
        payInfo = new PayInfoDTO();
        payInfo.ofCreate(
                1L,
                new PayInfoRequestDTO(
                        List.of(orderBookInfoDTO),
                        recipientInfoDTO,
                        addressInfoDTO,
                        LocalDate.now(),
                        "orderer",
                        "payType",
                        1000,
                        null,
                        1000,
                        10000
                )
        );

        paymentDTO = new PaymentDTO(1L, LocalDate.now(), 1000, PaymentStatus.DONE, "PAY1234");

        when(redisTemplate.opsForHash()).thenAnswer(invocation -> hashOperations);
    }

    @Test
    void testProcessOrder_Member_Success() {
        Long memberId = 1L;
        String guestId = null;

        when(hashOperations.get(anyString(), anyString())).thenReturn(payInfo);

        // Mock OrderService and RabbitService
        when(orderService.saveOrderInfo(eq(memberId), any(PayInfoDTO.class), any(PaymentDTO.class), any(OrderGroupCreateRequestDTO.class)))
                .thenReturn(123L);

        assertDoesNotThrow(() -> orderSaveProcessing.processOrder(memberId, guestId, paymentDTO));

        // Verify interactions
        verify(orderService, times(1)).saveOrderInfo(eq(memberId), eq(payInfo), eq(paymentDTO), any(OrderGroupCreateRequestDTO.class));
        verify(rabbitService, times(1)).sendCartMessage(eq(memberId), eq(null), eq(payInfo.getBookOrderDetails()));
        verify(rabbitService, times(1)).sendPointMessage(eq(memberId), eq(123L), eq(payInfo.getTotalAmount()));
    }

    @Test
    void testProcessOrder_Guest_Success() {
        Long memberId = null;
        String guestId = "guest123";

        when(hashOperations.get(anyString(), anyString())).thenReturn(payInfo);

        assertDoesNotThrow(() -> orderSaveProcessing.processOrder(memberId, guestId, paymentDTO));

        // Verify interactions
        verify(orderService, times(1)).saveOrderInfo(eq(OrderSaveProcessing.GUEST_USER_ID), eq(payInfo), eq(paymentDTO), any(OrderGroupCreateRequestDTO.class));
        verify(rabbitService, times(1)).sendCartMessage(eq(null), eq(guestId), eq(payInfo.getBookOrderDetails()));
    }

    @Test
    void testProcessOrder_RedisNotFound() {
        Long memberId = 1L;
        String guestId = null;

        when(redisTemplate.opsForHash()).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class,
                () -> orderSaveProcessing.processOrder(memberId, guestId, paymentDTO));

        assertEquals(ErrorCode.REDIS_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testProcessOrder_PayInfoNotFound() {
        Long memberId = 1L;
        String guestId = "guest123";

        when(hashOperations.get(any(), any())).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class,
                () -> orderSaveProcessing.processOrder(memberId, guestId, paymentDTO));

        assertEquals(ErrorCode.REDIS_NOT_FOUND, exception.getErrorCode());
    }
}