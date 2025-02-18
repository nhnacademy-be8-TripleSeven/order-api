package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.wrapping.WrappingCreateRequestDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponseDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingUpdateRequestDTO;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import com.tripleseven.orderapi.service.wrapping.WrappingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WrappingServiceTest {

    @Mock
    private WrappingRepository wrappingRepository;

    @InjectMocks
    private WrappingServiceImpl wrappingService;

    private Wrapping wrapping;

    @BeforeEach
    void setUp() {
        wrapping = new Wrapping();
        ReflectionTestUtils.setField(wrapping, "id", 1L);
        wrapping.ofCreate("Test Wrapping", 100);
    }


    @Test
    void testGetWrappingById_Success() {
        when(wrappingRepository.findById(anyLong())).thenReturn(Optional.of(wrapping));

        WrappingResponseDTO response = wrappingService.getWrappingById(1L);


        assertNotNull(response);
        assertEquals("Test Wrapping", response.getName());
        assertEquals(100, response.getPrice());

        verify(wrappingRepository, times(1)).findById(1L);
    }

    @Test
    void testGetWrappingById_Fail() {
        when(wrappingRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> wrappingService.getWrappingById(1L));
    }

    @Test
    void testCreateWrapping_Success() {
        when(wrappingRepository.save(any())).thenReturn(wrapping);
        when(wrappingRepository.findById(any())).thenReturn(Optional.of(wrapping));

        WrappingResponseDTO response = wrappingService.createWrapping(new WrappingCreateRequestDTO("Test Wrapping", 100));

        assertNotNull(response);
        assertEquals("Test Wrapping", response.getName());
        assertEquals(100, response.getPrice());

        Optional<Wrapping> savedWrapping = wrappingRepository.findById(response.getId());
        assertTrue(savedWrapping.isPresent());
        assertEquals("Test Wrapping", savedWrapping.get().getName());
        assertEquals(100, savedWrapping.get().getPrice());
    }

    @Test
    void testCreateWrapping_Fail() {
        WrappingCreateRequestDTO requestDTO = new WrappingCreateRequestDTO(null, -1);

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> wrappingService.createWrapping(requestDTO));

        assertNotNull(exception.getMessage());
    }

    @Test
    void testUpdateWrapping_Success() {
        WrappingUpdateRequestDTO updateRequest = new WrappingUpdateRequestDTO("Updated Wrapping", 150);
        when(wrappingRepository.findById(1L)).thenReturn(Optional.of(wrapping));

        WrappingResponseDTO response = wrappingService.updateWrapping(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Updated Wrapping", response.getName());
        assertEquals(150, response.getPrice());
    }

    @Test
    void testUpdateWrapping_Fail() {
        WrappingUpdateRequestDTO updateRequest = new WrappingUpdateRequestDTO("Updated Wrapping", 150);
        when(wrappingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> wrappingService.updateWrapping(1L, updateRequest));
    }

    @Test
    void testDeleteWrapping_Success() {
        when(wrappingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(wrappingRepository).deleteById(1L);

        wrappingService.deleteWrapping(1L);

        verify(wrappingRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteWrapping_Fail() {
        when(wrappingRepository.existsById(1L)).thenReturn(false);

        assertThrows(CustomException.class, () -> wrappingService.deleteWrapping(1L));
    }

    @Test
    void testGetWrappingsToList_Success() {
        Wrapping wrapping1 = new Wrapping();
        ReflectionTestUtils.setField(wrapping1, "id", 1L);
        wrapping1.ofCreate("Wrapping 1", 100);

        Wrapping wrapping2 = new Wrapping();
        ReflectionTestUtils.setField(wrapping2, "id", 2L);
        wrapping2.ofCreate("Wrapping 2", 150);

        List<Wrapping> wrappingList = List.of(wrapping1, wrapping2);

        when(wrappingRepository.findAll()).thenReturn(wrappingList);

        List<WrappingResponseDTO> responseList = wrappingService.getWrappingsToList();

        assertNotNull(responseList);
        assertEquals(2, responseList.size());

        WrappingResponseDTO firstResponse = responseList.get(0);
        assertEquals(1L, firstResponse.getId());
        assertEquals("Wrapping 1", firstResponse.getName());
        assertEquals(100, firstResponse.getPrice());

        WrappingResponseDTO secondResponse = responseList.get(1);
        assertEquals(2L, secondResponse.getId());
        assertEquals("Wrapping 2", secondResponse.getName());
        assertEquals(150, secondResponse.getPrice());

        verify(wrappingRepository, times(1)).findAll();
    }

    @Test
    void testGetWrappingsToList_EmptyList() {
        when(wrappingRepository.findAll()).thenReturn(List.of());

        List<WrappingResponseDTO> responseList = wrappingService.getWrappingsToList();

        assertNotNull(responseList);
        assertTrue(responseList.isEmpty());

        verify(wrappingRepository, times(1)).findAll();
    }
}