package com.tripleseven.orderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.business.policy.DefaultPolicyService;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.dto.defaultpolicy.DefaultPolicyDTO;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;
import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.service.defaultdeliverypolicy.DefaultDeliveryPolicyService;
import com.tripleseven.orderapi.service.defaultpointpolicy.DefaultPointPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DefaultPolicyController.class)
class DefaultPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DefaultDeliveryPolicyService defaultDeliveryPolicyService;

    @MockitoBean
    private DefaultPointPolicyService defaultPointPolicyService;

    @MockitoBean
    private DefaultPolicyService defaultPolicyService;

    private PointPolicy pointPolicy;

    @BeforeEach
    void setUp() {
        pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("Default", 100, BigDecimal.ZERO);
        ReflectionTestUtils.setField(pointPolicy, "id", 1L);
    }

    @Test
    void testGetDefaultPolicies_Success() throws Exception {
        DefaultPolicyDTO defaultPolicyDTO = new DefaultPolicyDTO(List.of(), List.of());
        Mockito.when(defaultPolicyService.getDefaultPolicies()).thenReturn(defaultPolicyDTO);

        mockMvc.perform(get("/admin/orders/default-policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    void testUpdateDefaultPointPolicy_Success() throws Exception {
        DefaultPointPolicyUpdateRequestDTO requestDTO = new DefaultPointPolicyUpdateRequestDTO(1L, PointPolicyType.DEFAULT_BUY);
        Mockito.when(defaultPointPolicyService.updateDefaultPoint(any())).thenReturn(1L);

        mockMvc.perform(put("/admin/orders/default-policy/point")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1L));
    }

    @Test
    void testUpdateDefaultDeliveryPolicy_Success() throws Exception {
        DefaultDeliveryPolicyUpdateRequestDTO requestDTO = new DefaultDeliveryPolicyUpdateRequestDTO(1L, DeliveryPolicyType.DEFAULT);
        Mockito.when(defaultDeliveryPolicyService.updateDefaultDelivery(any())).thenReturn(1L);

        mockMvc.perform(put("/admin/orders/default-policy/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1L));
    }

    @Test
    void testGetDefaultPointPolicy_Success() throws Exception {

        PointPolicyType type = PointPolicyType.DEFAULT_BUY;
        DefaultPointPolicyDTO defaultPointPolicyDTO = new DefaultPointPolicyDTO(1L, type, pointPolicy);
        Mockito.when(defaultPointPolicyService.getDefaultPointPolicy(any(PointPolicyType.class))).thenReturn(defaultPointPolicyDTO);

        mockMvc.perform(get("/orders/default-policy/point")
                        .param("type", type.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Default"))
                .andExpect(jsonPath("$.amount").value(100));
    }

    @Test
    void testGetDefaultDeliveryPolicy_Success() throws Exception {
        DeliveryPolicyType type = DeliveryPolicyType.DEFAULT;
        DefaultDeliveryPolicyDTO defaultDeliveryPolicyDTO = new DefaultDeliveryPolicyDTO(1L, "Default", 10000, 1000, type);
        Mockito.when(defaultDeliveryPolicyService.getDefaultDeliveryPolicy(any(DeliveryPolicyType.class))).thenReturn(defaultDeliveryPolicyDTO);

        mockMvc.perform(get("/orders/default-policy/delivery")
                        .param("type", type.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Default"))
                .andExpect(jsonPath("$.minPrice").value(10000))
                .andExpect(jsonPath("$.price").value(1000));
    }
}