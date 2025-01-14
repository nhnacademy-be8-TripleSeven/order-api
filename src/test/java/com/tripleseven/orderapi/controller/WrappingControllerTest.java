package com.tripleseven.orderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.dto.wrapping.WrappingCreateRequestDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponseDTO;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.service.wrapping.WrappingService;
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

@WebMvcTest(WrappingController.class)
class WrappingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WrappingService wrappingService;

    private Wrapping wrapping1;

    private Wrapping wrapping2;

    @BeforeEach
    void setUp() {
        wrapping1 = new Wrapping();
        wrapping1.ofCreate("Gift Wrap", 5000);
        ReflectionTestUtils.setField(wrapping1, "id", 1L);

        wrapping2 = new Wrapping();
        wrapping2.ofCreate("Luxury Wrap", 10000);
        ReflectionTestUtils.setField(wrapping2, "id", 2L);
    }

    @Test
    void testGetAllWrappings_Success() throws Exception {


        List<WrappingResponseDTO> wrappingList = List.of(
                WrappingResponseDTO.fromEntity(wrapping1),
                WrappingResponseDTO.fromEntity(wrapping2)
        );

        Mockito.when(wrappingService.getWrappingsToList()).thenReturn(wrappingList);

        mockMvc.perform(get("/orders/wrappings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Gift Wrap"))
                .andExpect(jsonPath("$[0].price").value(5000))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Luxury Wrap"))
                .andExpect(jsonPath("$[1].price").value(10000));
    }

    @Test
    void testGetAllWrappings_NotFound() throws Exception {
        Mockito.when(wrappingService.getWrappingsToList()).thenReturn(List.of());

        mockMvc.perform(get("/orders/wrappings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void testCreateWrapping_Success() throws Exception {
        WrappingCreateRequestDTO requestDTO = new WrappingCreateRequestDTO("Gift Wrap", 5000);
        WrappingResponseDTO responseDTO = WrappingResponseDTO.fromEntity(wrapping1);

        Mockito.when(wrappingService.createWrapping(any(WrappingCreateRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/admin/wrappings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gift Wrap"))
                .andExpect(jsonPath("$.price").value(5000));
    }

    @Test
    void testCreateWrapping_BadRequest() throws Exception {
        WrappingCreateRequestDTO invalidRequest = new WrappingCreateRequestDTO(null, -500);

        mockMvc.perform(post("/admin/wrappings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteWrapping_Success() throws Exception {
        Long wrappingId = 1L;

        Mockito.doNothing().when(wrappingService).deleteWrapping(eq(wrappingId));

        mockMvc.perform(delete("/admin/wrappings/{id}", wrappingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteWrapping_NotFound() throws Exception {
        Long wrappingId = 1L;

        Mockito.doThrow(new CustomException(ErrorCode.ID_NOT_FOUND)).when(wrappingService).deleteWrapping(eq(wrappingId));

        mockMvc.perform(delete("/admin/wrappings/{id}", wrappingId))
                .andExpect(status().isNotFound());
    }
}
