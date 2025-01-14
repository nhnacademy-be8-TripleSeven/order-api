package com.tripleseven.orderapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripleseven.orderapi.dto.deliverycode.DeliveryCodeDTO;
import com.tripleseven.orderapi.dto.deliverycode.DeliveryCodeResponseDTO;
import com.tripleseven.orderapi.entity.deliverycode.DeliveryCode;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.repository.deliverycode.DeliveryCodeRepository;
import com.tripleseven.orderapi.service.deliverycode.DeliveryCodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryCodeServiceTest {

    @Mock
    private DeliveryCodeRepository deliveryCodeRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DeliveryCodeServiceImpl deliveryCodeService;

    private String mockUrl;

    @BeforeEach
    void setUp() {
        mockUrl = "https://info.sweettracker.co.kr/api";
    }

    @Test
    void saveDeliveryCode_success() throws Exception {

        String responseBody = "{ \"companies\": [ { \"code\": \"TEST1\", \"international\": true, \"name\": \"Test Delivery 1\" }, { \"code\": \"TEST2\", \"international\": false, \"name\": \"Test Delivery 2\" } ] }";
        DeliveryCodeResponseDTO mockResponseDTO = new DeliveryCodeResponseDTO(List.of(
                new DeliveryCodeDTO("TEST1", true, "Test Delivery 1"),
                new DeliveryCodeDTO("TEST2", false, "Test Delivery 2")
        ));

        when(restTemplate.exchange(
                eq(mockUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));
        when(objectMapper.readValue(responseBody, DeliveryCodeResponseDTO.class)).thenReturn(mockResponseDTO);

        deliveryCodeService.saveDeliveryCode(mockUrl);

        ArgumentCaptor<DeliveryCode> captor = ArgumentCaptor.forClass(DeliveryCode.class);
        verify(deliveryCodeRepository, times(2)).save(captor.capture());


        List<DeliveryCode> savedEntities = captor.getAllValues();
        assertEquals(2, savedEntities.size());
        assertEquals("TEST1", savedEntities.get(0).getId());
        assertEquals("Test Delivery 1", savedEntities.get(0).getName());
        assertTrue(savedEntities.get(0).isInternational());

        assertEquals("TEST2", savedEntities.get(1).getId());
        assertEquals("Test Delivery 2", savedEntities.get(1).getName());
        assertFalse(savedEntities.get(1).isInternational());
    }

    @Test
    void saveDeliveryCode_failure_fetchError() {
        assertThrows(CustomException.class, () -> deliveryCodeService.saveDeliveryCode(mockUrl));

        verify(deliveryCodeRepository, never()).save(any());
    }

    @Test
    void getDeliveryCodeToName_success() {
        DeliveryCode deliveryCode = new DeliveryCode();
        deliveryCode.ofCreate("TEST1", true, "Test Delivery 1");

        when(deliveryCodeRepository.findDeliveryCodeByName("Test Delivery 1"))
                .thenReturn(Optional.of(deliveryCode));

        String code = deliveryCodeService.getDeliveryCodeToName("Test Delivery 1");

        assertEquals("TEST1", code);
        verify(deliveryCodeRepository, times(1)).findDeliveryCodeByName("Test Delivery 1");
    }

    @Test
    void getDeliveryCodeToName_failure_notFound() {
        when(deliveryCodeRepository.findDeliveryCodeByName("Unknown Delivery"))
                .thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> deliveryCodeService.getDeliveryCodeToName("Unknown Delivery"));

        verify(deliveryCodeRepository, times(1)).findDeliveryCodeByName("Unknown Delivery");
    }
}
