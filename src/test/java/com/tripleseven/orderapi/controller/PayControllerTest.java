package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.service.pay.PayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PayControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PayService payService;

    @InjectMocks
    private PayController payController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(payController).build();
    }

    @Test
    void testGetPayPrice() throws Exception {
        Long orderId = 1L;
        Long expectedPayPrice = 50000L;

        // ✅ `payService.getPayPrice(orderId)`가 `expectedPayPrice`를 반환하도록 설정
        when(payService.getPayPrice(orderId)).thenReturn(expectedPayPrice);

        // ✅ MockMvc를 사용해 GET 요청 테스트
        mockMvc.perform(get("/orders/pay/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답 검증
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // 응답 타입 검증
                .andExpect(content().string(expectedPayPrice.toString())); // 반환된 결제 금액 검증

        // ✅ `payService.getPayPrice(orderId)`가 한 번 호출되었는지 검증
        verify(payService, times(1)).getPayPrice(orderId);
    }
}