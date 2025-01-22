package com.tripleseven.orderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.business.order.OrderService;
import com.tripleseven.orderapi.dto.order.*;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.pay.PayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderGroupController.class)
@ExtendWith(MockitoExtension.class)
class OrderGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderGroupService orderGroupService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private PayService payService;

    private OrderGroupResponseDTO mockOrderGroup;
    private Page<OrderViewsResponseDTO> mockPage;

    @BeforeEach
    void setUp() {
        mockOrderGroup = OrderGroupResponseDTO.builder()
                .id(1L)
                .userId(100L)
                .wrappingId(10L)
                .orderedName("Test Order")
                .orderedAt(LocalDate.now())
                .recipientName("Recipient Name")
                .recipientPhone("010-1234-5678")
                .recipientHomePhone("02-9876-5432")
                .deliveryPrice(3000L)
                .address("Test Address")
                .build();

        OrderViewsResponseDTO mockOrderView = new OrderViewsResponseDTO(
                1L, LocalDate.now(), "Sample Order", 50000L, 2,
                OrderStatus.PAYMENT_PENDING, "Orderer Name", "Recipient Name"
        );

        mockPage = new PageImpl<>(Collections.singletonList(mockOrderView), PageRequest.of(0, 10), 1);
    }

    @Test
    void testGetOrderGroupById() throws Exception {
        when(payService.getOrderId(anyLong())).thenReturn(1L);
        when(orderGroupService.getOrderGroupById(anyLong())).thenReturn(mockOrderGroup);

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/order-groups/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(100L))
                .andExpect(jsonPath("$.orderedName").value("Test Order"));
    }

    @Test
    void testGetOrderGroupPeriod() throws Exception {
        OrderManageRequestDTO requestDTO = new OrderManageRequestDTO(
                LocalDate.now().minusDays(7), LocalDate.now(), OrderStatus.SHIPPING);
        when(orderGroupService.getOrderGroupPeriodByUserId(anyLong(), any(), any())).thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/order-groups/period")
                        .header("X-USER", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetGuestOrderGroups() throws Exception {
        when(orderGroupService.getGuestOrderGroups(anyString())).thenReturn(List.of(mockOrderGroup));
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/order-groups")
                        .param("phone", "01012345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value(100L))
                .andExpect(jsonPath("$[0].orderedName").value("Test Order"));
    }

    @Test
    void testDeleteOrderGroup() throws Exception {
        doNothing().when(orderGroupService).deleteOrderGroup(anyLong());
        mockMvc.perform(MockMvcRequestBuilders.delete("/orders/order-groups/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetOrderGroupDetail() throws Exception {
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO(1L, OrderStatus.SHIPPING, "Test Book", 2, 1000L, 5000L);
        OrderGroupInfoDTO orderGroupInfoDTO = new OrderGroupInfoDTO(
                60000L, 5000L, 3000L, "Gift Wrap", 2000L, 62000L, 5000L, 1000L);
        DeliveryInfoDTO deliveryInfoDTO = new DeliveryInfoDTO(
                "Fast Delivery", 123456, LocalDate.now(), 1L, LocalDate.now().minusDays(2), "Orderer Name",
                "Recipient Name", "010-1234-5678", "123 Street, City", LocalDate.now().minusDays(1));
        OrderPayDetailDTO orderPayDetailDTO = new OrderPayDetailDTO(
                List.of(orderInfoDTO), orderGroupInfoDTO, deliveryInfoDTO, new OrderPayInfoDTO()
        );
        when(orderService.getOrderPayDetail(anyLong(), anyLong())).thenReturn(orderPayDetailDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/order-groups/1")
                        .header("X-USER", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderInfos[0].orderDetailId").value(1L))
                .andExpect(jsonPath("$.orderInfos[0].orderStatus").value("SHIPPING"))
                .andExpect(jsonPath("$.orderGroupInfoDTO.primeTotalPrice").value(60000L))
                .andExpect(jsonPath("$.deliveryInfo.invoiceNumber").value(123456));
    }

    @Test
    void testGetAdminOrderGroupPeriod() throws Exception {
        when(orderGroupService.getOrderGroupPeriod(any(), any())).thenReturn(mockPage);
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/orders/order-groups/period")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OrderManageRequestDTO(LocalDate.now().minusDays(7), LocalDate.now(), OrderStatus.SHIPPING))))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAdminOrderGroupDetail() throws Exception {
        OrderInfoDTO orderInfoDTO = new OrderInfoDTO(2L, OrderStatus.DELIVERED, "Admin Test Book", 1, 2000L, 7000L);
        OrderGroupInfoDTO orderGroupInfoDTO = new OrderGroupInfoDTO(
                70000L, 6000L, 3500L, "Luxury Wrap", 3000L, 73500L, 7000L, 1200L);
        DeliveryInfoDTO deliveryInfoDTO = new DeliveryInfoDTO(
                "Express Delivery", 654321, LocalDate.now(), 2L, LocalDate.now().minusDays(3), "Admin Orderer",
                "Admin Recipient", "010-9876-5432", "456 Avenue, Metropolis", LocalDate.now().minusDays(2));
        OrderPayDetailDTO orderPayDetailDTO = new OrderPayDetailDTO(
                List.of(orderInfoDTO), orderGroupInfoDTO, deliveryInfoDTO, new OrderPayInfoDTO()
        );
        when(orderService.getOrderPayDetailAdmin(anyLong())).thenReturn(orderPayDetailDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/orders/order-groups/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderInfos[0].orderDetailId").value(2L))
                .andExpect(jsonPath("$.orderInfos[0].orderStatus").value("DELIVERED"))
                .andExpect(jsonPath("$.orderGroupInfoDTO.primeTotalPrice").value(70000L))
                .andExpect(jsonPath("$.deliveryInfo.invoiceNumber").value(654321));
    }
}