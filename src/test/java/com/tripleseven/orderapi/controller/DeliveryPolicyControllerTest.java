package com.tripleseven.orderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyResponseDTO;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.service.deliverypolicy.DeliveryPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryPolicyController.class)
class DeliveryPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DeliveryPolicyService deliveryPolicyService;

    private DeliveryPolicy deliveryPolicy1;

    private DeliveryPolicy deliveryPolicy2;

    @BeforeEach
    void setUp() {
        deliveryPolicy1 = new DeliveryPolicy();
        deliveryPolicy1.ofCreate("Express", 10000);
        ReflectionTestUtils.setField(deliveryPolicy1, "id", 1L);

        deliveryPolicy2 = new DeliveryPolicy();
        deliveryPolicy2.ofCreate("Standard", 5000);
        ReflectionTestUtils.setField(deliveryPolicy2, "id", 2L);
    }

    @Test
    void testGetDeliveryPolicy_Success() throws Exception {
        Long deliveryPolicyId = 1L;
        DeliveryPolicyResponseDTO mockResponse = DeliveryPolicyResponseDTO.fromEntity(deliveryPolicy1);

        Mockito.when(deliveryPolicyService.getDeliveryPolicy(deliveryPolicyId)).thenReturn(mockResponse);

        mockMvc.perform(get("/admin/orders/delivery-policies/{id}", deliveryPolicyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deliveryPolicyId))
                .andExpect(jsonPath("$.name").value("Express"))
                .andExpect(jsonPath("$.price").value(10000));
    }

    @Test
    void testGetDeliveryPolicy_NotFound() throws Exception {
        Long deliveryPolicyId = 1L;

        Mockito.when(deliveryPolicyService.getDeliveryPolicy(deliveryPolicyId))
                .thenThrow(new CustomException(ErrorCode.ID_NOT_FOUND));

        mockMvc.perform(get("/admin/orders/delivery-policies/{id}", deliveryPolicyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateDeliveryPolicy_Success() throws Exception {
        DeliveryPolicyCreateRequestDTO request = new DeliveryPolicyCreateRequestDTO("Express", 10000);
        DeliveryPolicyResponseDTO mockResponse = DeliveryPolicyResponseDTO.fromEntity(deliveryPolicy1);

        Mockito.when(deliveryPolicyService.createDeliveryPolicy(any(DeliveryPolicyCreateRequestDTO.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/admin/orders/delivery-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Express"))
                .andExpect(jsonPath("$.price").value(10000));
    }

    @Test
    void testUpdateDeliveryPolicy_Success() throws Exception {
        Long deliveryPolicyId = 1L;

        DeliveryPolicyUpdateRequestDTO request = new DeliveryPolicyUpdateRequestDTO("Updated Express", 15000);

        DeliveryPolicy updateDeliveryPolicy = new DeliveryPolicy();
        updateDeliveryPolicy.ofCreate("Updated Express", 15000);
        ReflectionTestUtils.setField(updateDeliveryPolicy, "id", 1L);

        DeliveryPolicyResponseDTO mockResponse = DeliveryPolicyResponseDTO.fromEntity(updateDeliveryPolicy);

        Mockito.when(deliveryPolicyService.updateDeliveryPolicy(eq(deliveryPolicyId), any(DeliveryPolicyUpdateRequestDTO.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(put("/admin/orders/delivery-policies/{id}", deliveryPolicyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deliveryPolicyId))
                .andExpect(jsonPath("$.name").value("Updated Express"))
                .andExpect(jsonPath("$.price").value(15000));
    }

    @Test
    void testDeleteDeliveryPolicy_Success() throws Exception {
        Long deliveryPolicyId = 1L;

        Mockito.doNothing().when(deliveryPolicyService).deleteDeliveryPolicy(deliveryPolicyId);

        mockMvc.perform(delete("/admin/orders/delivery-policies/{id}", deliveryPolicyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllDeliveryPolicies_Success() throws Exception {
        List<DeliveryPolicyResponseDTO> mockResponse = List.of(
                DeliveryPolicyResponseDTO.fromEntity(deliveryPolicy1),
                DeliveryPolicyResponseDTO.fromEntity(deliveryPolicy2)
        );

        Mockito.when(deliveryPolicyService.getAllDeliveryPolicies()).thenReturn(mockResponse);

        mockMvc.perform(get("/admin/orders/delivery-policies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Express"))
                .andExpect(jsonPath("$[0].price").value(10000))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Standard"))
                .andExpect(jsonPath("$[1].price").value(5000));
    }

    @Test
    void testGetAllDeliveryPolicies_NotFound() throws Exception {
        Mockito.when(deliveryPolicyService.getAllDeliveryPolicies())
                .thenReturn(List.of());

        mockMvc.perform(get("/admin/orders/delivery-policies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}