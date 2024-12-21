package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequest;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyResponse;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequest;
import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.tripleseven.orderapi.repository.deliverypolicy.DeliveryPolicyRepository;
import com.tripleseven.orderapi.service.deliverypolicy.DeliveryPolicyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DeliveryPolicyServiceTest {
    @Mock
    private DeliveryPolicyRepository deliveryPolicyRepository;

    @InjectMocks
    private DeliveryPolicyServiceImpl deliveryPolicyService;

    private DeliveryPolicy deliveryPolicy;

    @BeforeEach
    void setUp() {
        deliveryPolicy = new DeliveryPolicy();
        deliveryPolicy.ofCreate("Test DeliveryPolicy", 1000);
    }

    @Test
    void testFindById_Success() {
        when(deliveryPolicyRepository.findById(anyLong())).thenReturn(Optional.of(deliveryPolicy));

        DeliveryPolicyResponse response = deliveryPolicyService.getDeliveryPolicy(1L);

        assertNotNull(response);
        assertEquals("Test DeliveryPolicy", response.getName());
        assertEquals(1000, response.getPrice());

        verify(deliveryPolicyRepository, times(1)).findById(1L);
    }
    @Test
    void testFindById_Fail() {
        when(deliveryPolicyRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> deliveryPolicyService.getDeliveryPolicy(1L));
        verify(deliveryPolicyRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateDeliveryPolicy_Success() {
        when(deliveryPolicyRepository.save(any())).thenReturn(deliveryPolicy);
        when(deliveryPolicyRepository.findById(any())).thenReturn(Optional.of(deliveryPolicy));

        DeliveryPolicyResponse response = deliveryPolicyService.createDeliveryPolicy(
                new DeliveryPolicyCreateRequest(
                        deliveryPolicy.getName(),
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
        assertThrows(RuntimeException.class, () -> deliveryPolicyService.createDeliveryPolicy(
                new DeliveryPolicyCreateRequest(
                        null,
                        -1)));
    }

    @Test
    void testUpdateDeliveryPolicy_Success() {
        DeliveryPolicyUpdateRequest updateRequest = new DeliveryPolicyUpdateRequest("Updated DeliveryPolicy", 1500);
        when(deliveryPolicyRepository.findById(1L)).thenReturn(Optional.of(deliveryPolicy));

        DeliveryPolicyResponse response = deliveryPolicyService.updateDeliveryPolicy(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Updated DeliveryPolicy", response.getName());
        assertEquals(1500, response.getPrice());
        verify(deliveryPolicyRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateDeliveryPolicy_Fail() {
        DeliveryPolicyUpdateRequest updateRequest = new DeliveryPolicyUpdateRequest("Updated DeliveryPolicy", 1500);
        when(deliveryPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> deliveryPolicyService.updateDeliveryPolicy(1L, updateRequest));
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
        assertThrows(RuntimeException.class, () -> deliveryPolicyService.deleteDeliveryPolicy(1L));

        verify(deliveryPolicyRepository, times(0)).deleteById(1L);
    }
}
