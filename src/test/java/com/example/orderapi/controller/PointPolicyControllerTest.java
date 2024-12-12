package com.example.orderapi.controller;

import com.example.orderapi.dto.pointpolicy.PointPolicyCreateRequest;
import com.example.orderapi.dto.pointpolicy.PointPolicyUpdateRequest;
import com.example.orderapi.entity.PointPolicy.PointPolicy;
import com.example.orderapi.service.PointPolicyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointPolicyController.class)
class PointPolicyControllerTest {

    @MockBean
    PointPolicyService pointPolicyService;

    @Autowired
    MockMvc mockMvc;

    PointPolicy pointPolicy;
    PointPolicy pointPolicy2;

    ObjectMapper objectMapper;

    List<PointPolicy> pointPolicies;
    @BeforeEach
    void setUp() {
        pointPolicy = new PointPolicy();
        pointPolicy = new PointPolicy();
        pointPolicy.setId(1L);
        pointPolicy.setName("Welcome Bonus");
        pointPolicy.setAmount(100);
        pointPolicy.setRate(BigDecimal.valueOf(0.1));

        pointPolicy2 = new PointPolicy();
        pointPolicy2.setId(2L);
        pointPolicy2.setAmount(350);
        pointPolicy2.setRate(BigDecimal.valueOf(2.5));
        pointPolicy2.setName("test policy2");

        pointPolicies = new ArrayList<>();
        pointPolicies.add(pointPolicy);
        pointPolicies.add(pointPolicy2);

        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllPointPolicies() throws Exception {
        when(pointPolicyService.findAll()).thenReturn(pointPolicies);

        mockMvc.perform(get("/point-policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Welcome Bonus"))
                .andExpect(jsonPath("$[0].amount").value(100))
                .andExpect(jsonPath("$[0].rate").value(0.1))
                .andExpect(jsonPath("$[1].name").value("test policy2"))
                .andExpect(jsonPath("$[1].amount").value(350))
                .andExpect(jsonPath("$[1].rate").value(2.5));
    }

    @Test
    void getPointPolicy() throws Exception {
        when(pointPolicyService.findById(2L)).thenReturn(pointPolicy2);

        mockMvc.perform(get("/point-policies/{pointPolicyId}",2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test policy2"))
                .andExpect(jsonPath("$.amount").value(350))
                .andExpect(jsonPath("$.rate").value(2.5));
    }

    @Test
    void createPointPolicy() throws Exception {
        PointPolicyCreateRequest pointPolicyCreateRequest = new PointPolicyCreateRequest();
        pointPolicyCreateRequest.setName("Welcome Bonus");
        pointPolicyCreateRequest.setAmount(100);
        pointPolicyCreateRequest.setRate(BigDecimal.valueOf(0.1));

        mockMvc.perform(post("/point-policies")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pointPolicyCreateRequest)))
                .andExpect(status().isCreated());


    }

    @Test
    void deletePointPolicy() throws Exception {
        mockMvc.perform(delete("/point-policies/{pointPolicyId}",1))
                .andExpect(status().isNoContent());

    }

    @Test
    void updatePointPolicy() throws Exception {
        PointPolicyUpdateRequest pointPolicyUpdateRequest = new PointPolicyUpdateRequest();
        pointPolicyUpdateRequest.setName("Welcome Bonus");
        pointPolicyUpdateRequest.setAmount(100);
        pointPolicyUpdateRequest.setRate(BigDecimal.valueOf(0.1));

        when(pointPolicyService.findById(1L)).thenReturn(pointPolicy);

        mockMvc.perform(put("/point-policies/{pointPolicyId}",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pointPolicyUpdateRequest)))
                .andExpect(status().isNoContent());
    }
}