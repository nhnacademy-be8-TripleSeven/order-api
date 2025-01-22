package com.tripleseven.orderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.business.point.PointService;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PointService pointService;

    private PointHistory pointHistory;

    private Long memberId;

    @BeforeEach
    void setUp() {
        pointHistory = PointHistory.ofCreate(
                HistoryTypes.EARN,
                100,
                "Registration Reward",
                memberId
        );
        ReflectionTestUtils.setField(pointHistory, "id", 1L);
    }

    @Test
    void testCreateRegisterPointHistory_Success() throws Exception {
        PointHistoryResponseDTO responseDTO = PointHistoryResponseDTO.fromEntity(pointHistory);
        Mockito.when(pointService.createRegisterPointHistory(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(post("/points/default-policy/register")
                        .header("X-USER", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.comment").value("Registration Reward"))
                .andExpect(jsonPath("$.types").value(HistoryTypes.EARN.toString()));
    }

    @Test
    void testCreateRegisterPointHistory_BadRequest_MissingHeader() throws Exception {
        mockMvc.perform(post("/points/default-policy/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateRegisterPointHistory_Fail_UserNotFound() throws Exception {
        Mockito.doThrow(new CustomException(ErrorCode.ID_NOT_FOUND)).when(pointService).createRegisterPointHistory(anyLong());

        mockMvc.perform(post("/points/default-policy/register")
                        .header("X-USER", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
