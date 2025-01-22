package com.tripleseven.orderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryCreateRequestDTO;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryPageResponseDTO;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;
import com.tripleseven.orderapi.dto.pointhistory.UserPointHistoryDTO;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.service.pointhistory.PointHistoryService;
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
import java.time.LocalDateTime;
import java.util.Collections;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointHistoryController.class)
@ExtendWith(MockitoExtension.class)
class PointHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PointHistoryService pointHistoryService;

    private Page<PointHistoryResponseDTO> mockPage;

    @BeforeEach
    void setUp() {
        PointHistoryResponseDTO mockResponse = PointHistoryResponseDTO.builder()
                .id(1L)
                .types(HistoryTypes.EARN)
                .amount(1000L)
                .changedAt(LocalDateTime.now())
                .comment("Test Comment")
                .build();
        mockPage = new PageImpl<>(Collections.singletonList(mockResponse), PageRequest.of(0, 10), 1);
    }

    @Test
    void testFindPointHistories() throws Exception {
        when(pointHistoryService.getPointHistories(any())).thenReturn(mockPage);
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/point-histories"))
                .andExpect(status().isOk());
    }

    @Test
    void testFindByMemberId() throws Exception {
        when(pointHistoryService.getPointHistoriesByMemberId(anyLong(), any())).thenReturn(mockPage);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/point-histories")
                        .header("X-USER", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePointHistoryByMemberId() throws Exception {
        doNothing().when(pointHistoryService).removePointHistoriesByMemberId(anyLong());
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/point-histories")
                        .header("X-USER", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeletePointHistoryByPointHistoryId() throws Exception {
        doNothing().when(pointHistoryService).removePointHistoryById(anyLong());
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/point-histories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCreateFromRequest() throws Exception {
        PointHistoryCreateRequestDTO requestDTO = new PointHistoryCreateRequestDTO(HistoryTypes.EARN, 1L);
        PointHistoryResponseDTO responseDTO = PointHistoryResponseDTO.builder()
                .id(1L)
                .types(requestDTO.getTypes())
                .amount(1000L)
                .changedAt(LocalDateTime.now())
                .comment("Test Comment")
                .build();
        when(pointHistoryService.createPointHistory(anyLong(), any())).thenReturn(responseDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/point-histories")
                        .header("X-USER", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.types").value(requestDTO.getTypes().toString()))
                .andExpect(jsonPath("$.comment").value("Test Comment"));
    }

    @Test
    void testGetPoint() throws Exception {
        when(pointHistoryService.getTotalPointByMemberId(anyLong())).thenReturn(5000);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/point-histories/point")
                        .header("X-USER", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5000));
    }

    @Test
    void testGetUserPointHistories() throws Exception {
        PointHistoryPageResponseDTO<UserPointHistoryDTO> mockResponse = new PointHistoryPageResponseDTO<>();
        when(pointHistoryService.getUserPointHistories(anyLong(), any(), any(), any())).thenReturn(mockResponse);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/point-histories")
                        .header("X-USER", "1"))
                .andExpect(status().isOk());
    }
}
