package com.tripleseven.orderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailUpdateRequestDTO;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderDetailController.class)
@ExtendWith(MockitoExtension.class)
class OrderDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderDetailService orderDetailService;

    private OrderDetailResponseDTO mockOrderDetail;

    @BeforeEach
    void setUp() {
        mockOrderDetail = OrderDetailResponseDTO.builder()
                .id(1L)
                .bookId(101L)
                .quantity(2)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .primePrice(5000L)
                .discountPrice(1000L)
                .orderGroupId(10L)
                .build();
    }

    @Test
    void testGetOrderDetailsByOrderGroupId() throws Exception {
        when(orderDetailService.getOrderDetailsToList(anyLong())).thenReturn(List.of(mockOrderDetail));

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/order-details/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].bookId").value(101L))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].orderStatus").value("PAYMENT_COMPLETED"))
                .andExpect(jsonPath("$[0].primePrice").value(5000L))
                .andExpect(jsonPath("$[0].discountPrice").value(1000L))
                .andExpect(jsonPath("$[0].orderGroupId").value(10L));
    }

    @Test
    void testDeleteOrderDetail() throws Exception {
        doNothing().when(orderDetailService).deleteOrderDetail(anyLong());
        mockMvc.perform(MockMvcRequestBuilders.delete("/orders/order-details/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCheckUserPurchase() throws Exception {
        when(orderDetailService.hasUserPurchasedBook(anyLong(), anyLong())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/order-details/check-purchase")
                        .param("userId", "1")
                        .param("bookId", "101"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }



}