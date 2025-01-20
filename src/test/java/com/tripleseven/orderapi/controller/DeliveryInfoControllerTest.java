package com.tripleseven.orderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponseDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoUpdateRequestDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.service.deliveryinfo.DeliveryInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryInfoController.class)
class DeliveryInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryInfoService deliveryInfoService;

    @Autowired
    private ObjectMapper objectMapper;

    private DeliveryInfo deliveryInfo;

    @BeforeEach
    void setUp() {
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", "01012345678", 1000, "Test Address", null);
        ReflectionTestUtils.setField(orderGroup, "id", 1L);

        deliveryInfo = new DeliveryInfo();
        deliveryInfo.ofCreate(orderGroup, LocalDate.now());
        deliveryInfo.ofUpdate("Test DeliveryInfo", 12345678, LocalDate.now());
        deliveryInfo.ofShippingUpdate(LocalDate.now());
        ReflectionTestUtils.setField(deliveryInfo, "id", 1L);
    }

    @Test
    void testGetDeliveryInfo_Success() throws Exception {
        Long deliveryInfoId = 1L;
        DeliveryInfoResponseDTO mockResponse = DeliveryInfoResponseDTO.fromEntity(deliveryInfo);

        Mockito.when(deliveryInfoService.getDeliveryInfoById(deliveryInfoId)).thenReturn(mockResponse);

        mockMvc.perform(get("/orders/delivery-info/{id}", deliveryInfoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deliveryInfoId))
                .andExpect(jsonPath("$.name").value("Test DeliveryInfo"))
                .andExpect(jsonPath("$.invoiceNumber").value(12345678))
                .andExpect(jsonPath("$.arrivedAt").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.shippingAt").value(LocalDate.now().toString()));
    }

    @Test
    void testGetDeliveryInfo_NotFound() throws Exception {
        Long deliveryInfoId = 1L;

        Mockito.when(deliveryInfoService.getDeliveryInfoById(deliveryInfoId))
                .thenThrow(new CustomException(ErrorCode.ID_NOT_FOUND));

        mockMvc.perform(get("/orders/delivery-info/{id}", deliveryInfoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateDeliveryArrivedAt_Success() throws Exception {
        Long deliveryInfoId = 1L;
        DeliveryInfoUpdateRequestDTO request = new DeliveryInfoUpdateRequestDTO("CJ대한통운", 12345678, LocalDate.now());

        deliveryInfo.ofUpdate("CJ대한통운", 12345678, LocalDate.now());
        deliveryInfo.ofShippingUpdate(LocalDate.now());

        DeliveryInfoResponseDTO mockResponse = DeliveryInfoResponseDTO.fromEntity(deliveryInfo);

        Mockito.when(deliveryInfoService.updateDeliveryInfo(eq(deliveryInfoId), any(DeliveryInfoUpdateRequestDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(put("/orders/delivery-info/{id}/arrived-at", deliveryInfoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deliveryInfoId))
                .andExpect(jsonPath("$.name").value("CJ대한통운"))
                .andExpect(jsonPath("$.invoiceNumber").value(12345678))
                .andExpect(jsonPath("$.arrivedAt").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.shippingAt").value(LocalDate.now().toString()));
    }

    @Test
    void testUpdateDeliveryArrivedAt_NotFound() throws Exception {
        Long deliveryInfoId = 1L;
        DeliveryInfoUpdateRequestDTO request = new DeliveryInfoUpdateRequestDTO("CJ대한통운", 12345678, LocalDate.now());

        Mockito.when(deliveryInfoService.updateDeliveryInfo(eq(deliveryInfoId), any(DeliveryInfoUpdateRequestDTO.class)))
                .thenThrow(new CustomException(ErrorCode.ID_NOT_FOUND));

        mockMvc.perform(put("/orders/delivery-info/{id}/arrived-at", deliveryInfoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteDeliveryInfo_Success() throws Exception {
        Long deliveryInfoId = 1L;

        Mockito.doNothing().when(deliveryInfoService).deleteDeliveryInfo(deliveryInfoId);

        mockMvc.perform(delete("/admin/orders/delivery-info/{id}", deliveryInfoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteDeliveryInfo_NotFound() throws Exception {
        Long deliveryInfoId = 1L;

        Mockito.doThrow(new CustomException(ErrorCode.ID_NOT_FOUND)).when(deliveryInfoService).deleteDeliveryInfo(deliveryInfoId);

        mockMvc.perform(delete("/admin/orders/delivery-info/{id}", deliveryInfoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}