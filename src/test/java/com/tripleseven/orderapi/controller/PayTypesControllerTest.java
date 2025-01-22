package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.dto.paytypes.PayTypeCreateRequestDTO;
import com.tripleseven.orderapi.dto.paytypes.PayTypesResponseDTO;
import com.tripleseven.orderapi.service.paytypes.PayTypesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PayTypesControllerTest {

    @Mock
    private PayTypesService payTypesService;

    @InjectMocks
    private PayTypesController payTypesController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(payTypesController).build();
    }

    @Test
    void getAllPayTypes_ShouldReturnListOfPayTypes() throws Exception {
        // Given
        PayTypesResponseDTO payType1 = PayTypesResponseDTO.builder().id(1L).name("CARD").build();
        PayTypesResponseDTO payType2 = PayTypesResponseDTO.builder().id(2L).name("BANK_TRANSFER").build();

        when(payTypesService.getAllPayTypes()).thenReturn(List.of(payType1, payType2));

        // When & Then
        mockMvc.perform(get("/pay-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("CARD"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("BANK_TRANSFER"));
    }

    @Test
    void getPayTypeById_ShouldReturnPayType_WhenIdExists() throws Exception {
        // Given
        PayTypesResponseDTO payType = PayTypesResponseDTO.builder().id(1L).name("CARD").build();

        when(payTypesService.getPayTypeById(1L)).thenReturn(payType);

        // When & Then
        mockMvc.perform(get("/pay-types/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("CARD"));
    }

    @Test
    void createPayType_ShouldReturnCreatedPayType() throws Exception {
        // Given
        PayTypeCreateRequestDTO requestDTO = new PayTypeCreateRequestDTO("PAYPAL");
        PayTypesResponseDTO responseDTO = PayTypesResponseDTO.builder().id(3L).name("PAYPAL").build();

        when(payTypesService.createPayType(any(PayTypeCreateRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/admin/pay-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"PAYPAL\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("PAYPAL"));
    }

    @Test
    void updatePayType_ShouldReturnUpdatedPayType() throws Exception {
        // Given
        PayTypeCreateRequestDTO requestDTO = new PayTypeCreateRequestDTO("BITCOIN");
        PayTypesResponseDTO responseDTO = PayTypesResponseDTO.builder().id(2L).name("BITCOIN").build();

        when(payTypesService.updatePayType(eq(2L), any(PayTypeCreateRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(put("/admin/pay-types/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"BITCOIN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("BITCOIN"));
    }

    @Test
    void removePayType_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(payTypesService).removePayType(3L);

        // When & Then
        mockMvc.perform(delete("/admin/pay-types/{id}", 3L))
                .andExpect(status().isNoContent());
    }

    @Test
    void payTypesResponseDTO_ShouldThrowException_WhenIdIsNull() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> PayTypesResponseDTO.builder().id(null).name("TEST").build()
        );
        assertEquals("id can not be null", exception.getMessage());
    }

    @Test
    void payTypesResponseDTO_ShouldThrowException_WhenNameIsNull() {
        // Given & When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> PayTypesResponseDTO.builder().id(1L).name(null).build()
        );
        assertEquals("name can not be null", exception.getMessage());
    }
}