package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyResponseDTO;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.repository.deliverypolicy.DeliveryPolicyRepository;
import com.tripleseven.orderapi.service.deliverypolicy.DeliveryPolicyServiceImpl;
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
class DeliveryPolicyServiceTest {
    @Mock
    private DeliveryPolicyRepository deliveryPolicyRepository;

    @InjectMocks
    private DeliveryPolicyServiceImpl deliveryPolicyService;

    private DeliveryPolicy deliveryPolicy;

    @BeforeEach
    void setUp() {
        deliveryPolicy = new DeliveryPolicy();
        ReflectionTestUtils.setField(deliveryPolicy, "id", 1L);
        deliveryPolicy.ofCreate("Test DeliveryPolicy", 10000, 1000);
    }

    @Test
    void testFindById_Success() {
        when(deliveryPolicyRepository.findById(anyLong())).thenReturn(Optional.of(deliveryPolicy));

        DeliveryPolicyResponseDTO response = deliveryPolicyService.getDeliveryPolicy(1L);

        assertNotNull(response);
        assertEquals("Test DeliveryPolicy", response.getName());
        assertEquals(1000, response.getPrice());

        verify(deliveryPolicyRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_Fail() {
        when(deliveryPolicyRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> deliveryPolicyService.getDeliveryPolicy(1L));
        verify(deliveryPolicyRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateDeliveryPolicy_Success() {
        when(deliveryPolicyRepository.save(any())).thenReturn(deliveryPolicy);
        when(deliveryPolicyRepository.findById(any())).thenReturn(Optional.of(deliveryPolicy));

        DeliveryPolicyResponseDTO response = deliveryPolicyService.createDeliveryPolicy(
                new DeliveryPolicyCreateRequestDTO(
                        deliveryPolicy.getName(),
                        deliveryPolicy.getMinPrice(),
                        deliveryPolicy.getPrice()
                ));

        assertNotNull(response);
        assertEquals("Test DeliveryPolicy", response.getName());
        assertEquals(1000, response.getPrice());

        Optional<DeliveryPolicy> savedDeliveryPolicy = deliveryPolicyRepository.findById(response.getId());
        assertTrue(savedDeliveryPolicy.isPresent());
        assertEquals("Test DeliveryPolicy", savedDeliveryPolicy.get().getName());
        assertEquals(1000, savedDeliveryPolicy.get().getPrice());

        verify(deliveryPolicyRepository, times(1)).findById(response.getId());
        verify(deliveryPolicyRepository, times(1)).save(any());
    }

    @Test
    void testCreateDeliveryPolicy_Fail() {
        DeliveryPolicyCreateRequestDTO requestDTO = new DeliveryPolicyCreateRequestDTO(null, -1, -1);

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> deliveryPolicyService.createDeliveryPolicy(requestDTO));

        assertNotNull(exception.getMessage());
    }

    @Test
    void testUpdateDeliveryPolicy_Success() {
        DeliveryPolicyUpdateRequestDTO updateRequest = new DeliveryPolicyUpdateRequestDTO("Updated DeliveryPolicy", 15000, 1500);
        when(deliveryPolicyRepository.findById(1L)).thenReturn(Optional.of(deliveryPolicy));

        DeliveryPolicyResponseDTO response = deliveryPolicyService.updateDeliveryPolicy(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Updated DeliveryPolicy", response.getName());
        assertEquals(1500, response.getPrice());
        verify(deliveryPolicyRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateDeliveryPolicy_Fail() {
        DeliveryPolicyUpdateRequestDTO updateRequest = new DeliveryPolicyUpdateRequestDTO("Updated DeliveryPolicy", 15000, 1500);
        when(deliveryPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> deliveryPolicyService.updateDeliveryPolicy(1L, updateRequest));
        verify(deliveryPolicyRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteDeliveryPolicy_Success() {
        when(deliveryPolicyRepository.existsById(1L)).thenReturn(true);
        doNothing().when(deliveryPolicyRepository).deleteById(1L);

        deliveryPolicyService.deleteDeliveryPolicy(1L);

        verify(deliveryPolicyRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDeliveryPolicy_Fail() {
        when(deliveryPolicyRepository.existsById(1L)).thenReturn(false);
        assertThrows(CustomException.class, () -> deliveryPolicyService.deleteDeliveryPolicy(1L));

        verify(deliveryPolicyRepository, times(0)).deleteById(1L);
    }

    @Test
    void testGetAllDeliveryPolicies_Success() {
        when(deliveryPolicyRepository.findAll()).thenReturn(List.of(deliveryPolicy, deliveryPolicy));

        List<DeliveryPolicyResponseDTO> responses = deliveryPolicyService.getAllDeliveryPolicies();

        assertEquals(2, responses.size());
        assertEquals("Test DeliveryPolicy", responses.get(0).getName());
        assertEquals(1000, responses.get(0).getPrice());
        assertEquals("Test DeliveryPolicy", responses.get(1).getName());
        assertEquals(1000, responses.get(1).getPrice());

        // Verifying the repository interaction
        verify(deliveryPolicyRepository, times(1)).findAll();
    }
}
