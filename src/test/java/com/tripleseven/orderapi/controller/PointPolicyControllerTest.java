package com.tripleseven.orderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyCreateRequestDTO;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyResponseDTO;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.service.pointpolicy.PointPolicyService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointPolicyController.class)
class PointPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PointPolicyService pointPolicyService;

    @Autowired
    private ObjectMapper objectMapper;

    private PointPolicy pointPolicy1;

    private PointPolicy pointPolicy2;

    @BeforeEach
    void setUp() {
        pointPolicy1 = new PointPolicy();
        pointPolicy1.ofCreate("Policy A", 100, BigDecimal.ZERO);
        ReflectionTestUtils.setField(pointPolicy1, "id", 1L);

        pointPolicy2 = new PointPolicy();
        pointPolicy2.ofCreate("Policy B", 0, BigDecimal.valueOf(0.2));
        ReflectionTestUtils.setField(pointPolicy2, "id", 2L);
    }

    @Test
    void testGetAllPointPolicies_Success() throws Exception {
        List<PointPolicyResponseDTO> mockResponse = List.of(
                PointPolicyResponseDTO.fromEntity(pointPolicy1),
                PointPolicyResponseDTO.fromEntity(pointPolicy2)
        );

        Mockito.when(pointPolicyService.findAll()).thenReturn(mockResponse);

        mockMvc.perform(get("/admin/orders/point-policies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Policy A"))
                .andExpect(jsonPath("$[0].amount").value(100))
                .andExpect(jsonPath("$[0].rate").value(BigDecimal.ZERO))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void testGetPointPolicy_Success() throws Exception {
        PointPolicyResponseDTO mockResponse = PointPolicyResponseDTO.fromEntity(pointPolicy1);

        Mockito.when(pointPolicyService.findById(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/admin/orders/point-policies/{pointPolicyId}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Policy A"))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.rate").value(BigDecimal.ZERO));
    }

    @Test
    void testCreatePointPolicy_Success() throws Exception {
        PointPolicyCreateRequestDTO request = new PointPolicyCreateRequestDTO("Policy A", 100, BigDecimal.ZERO);
        PointPolicyResponseDTO mockResponse = new PointPolicyResponseDTO(1L, "Policy A", 100, BigDecimal.ZERO);

        Mockito.when(pointPolicyService.save(any(PointPolicyCreateRequestDTO.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/admin/orders/point-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Policy A"))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.rate").value(BigDecimal.ZERO));
    }

    @Test
    void testUpdatePointPolicy_Success() throws Exception {
        PointPolicyUpdateRequestDTO request = new PointPolicyUpdateRequestDTO("Policy A Updated", 200, BigDecimal.ZERO);
        PointPolicyResponseDTO mockResponse = new PointPolicyResponseDTO(1L, "Policy A Updated", 200, BigDecimal.ZERO);

        Mockito.when(pointPolicyService.update(eq(1L), any(PointPolicyUpdateRequestDTO.class))).thenReturn(mockResponse);

        mockMvc.perform(put("/admin/orders/point-policies/{pointPolicyId}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Policy A Updated"))
                .andExpect(jsonPath("$.amount").value(200))
                .andExpect(jsonPath("$.rate").value(BigDecimal.ZERO));
    }

    @Test
    void testDeletePointPolicy_Success() throws Exception {
        Mockito.doNothing().when(pointPolicyService).delete(1L);

        mockMvc.perform(delete("/admin/orders/point-policies/{pointPolicyId}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetPointPolicy_NotFound() throws Exception {
        Mockito.when(pointPolicyService.findById(anyLong()))
                .thenThrow(new CustomException(ErrorCode.ID_NOT_FOUND));

        mockMvc.perform(get("/admin/orders/point-policies/{pointPolicyId}", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}