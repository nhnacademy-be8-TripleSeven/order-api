package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.order.OrderService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void getNetAmount_ShouldReturnAmount_WhenUserIdExists() throws Exception {
        // Given
        Long userId = 1L;
        Long expectedAmount = 150000L;

        when(orderService.getThreeMonthsNetAmount(userId)).thenReturn(expectedAmount);

        // When & Then
        mockMvc.perform(get("/orders/amount/net")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedAmount));
    }

    @Test
    void getNetAmount_ShouldReturnBadRequest_WhenUserIdIsMissing() throws Exception {
        // When & Then
        mockMvc.perform(get("/orders/amount/net")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}