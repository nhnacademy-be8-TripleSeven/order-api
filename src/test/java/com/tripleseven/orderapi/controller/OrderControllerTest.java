package com.tripleseven.orderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.business.pay.OrderProcessingStrategy;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderProcessingStrategy orderProcessingStrategy;

    @Test
    void testSaveOrderHistoryForMember() throws Exception {
        Long userId = 1L;
        OrderGroupCreateRequestDTO request =
                new OrderGroupCreateRequestDTO(
                        1L,
                        "Test Order",
                        "Test Recipient",
                        "010-1234-5678",
                        "010-1234-5678",
                        5000,
                        "Test Address"
                );

        Mockito.doNothing().when(orderProcessingStrategy)
                        .processMemberOrder(eq(userId), any(OrderGroupCreateRequestDTO.class));

        mockMvc.perform(post("/api/orders/process")
                        .header("X-USER", userId)
                        .cookie(new Cookie("GUEST-ID", "guest-123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(orderProcessingStrategy, Mockito.times(1))
                .processMemberOrder(eq(userId), any(OrderGroupCreateRequestDTO.class));
        Mockito.verify(orderProcessingStrategy, Mockito.never())
                .processNonMemberOrder(any(String.class), any(OrderGroupCreateRequestDTO.class));
    }

    @Test
    void testSaveOrderHistoryForNonMember() throws Exception {
        String guestId = "guest-123";
        OrderGroupCreateRequestDTO request =
                new OrderGroupCreateRequestDTO(
                        1L,
                        "Test Order",
                        "Test Recipient",
                        "010-1234-5678",
                        "010-1234-5678",
                        5000,
                        "Test Address"
                );

        Mockito.doNothing().when(orderProcessingStrategy).processNonMemberOrder(eq(guestId), any(OrderGroupCreateRequestDTO.class));

        mockMvc.perform(post("/api/orders/process")
                        .cookie(new Cookie("GUEST-ID", guestId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(orderProcessingStrategy, Mockito.times(1))
                .processNonMemberOrder(eq(guestId), any(OrderGroupCreateRequestDTO.class));
        Mockito.verify(orderProcessingStrategy, Mockito.never())
                .processMemberOrder(any(Long.class), any(OrderGroupCreateRequestDTO.class));
    }

    @Test
    void testSaveOrderHistoryWithoutGuestId() throws Exception {
        OrderGroupCreateRequestDTO request =
                new OrderGroupCreateRequestDTO(
                        1L,
                        "Test Order",
                        "Test Recipient",
                        "010-1234-5678",
                        "010-1234-5678",
                        5000,
                        "Test Address"
                );

        mockMvc.perform(post("/api/orders/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());

        Mockito.verify(orderProcessingStrategy, Mockito.never())
                .processMemberOrder(any(Long.class), any(OrderGroupCreateRequestDTO.class));
        Mockito.verify(orderProcessingStrategy, Mockito.never())
                .processNonMemberOrder(any(String.class), any(OrderGroupCreateRequestDTO.class));
    }
}
