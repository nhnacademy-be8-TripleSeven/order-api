package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.business.policy.DefaultPolicyServiceImpl;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.dto.defaultpolicy.DefaultPolicyDTO;
import com.tripleseven.orderapi.service.defaultdeliverypolicy.DefaultDeliveryPolicyService;
import com.tripleseven.orderapi.service.defaultpointpolicy.DefaultPointPolicyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultPolicyServiceTest {

    @Mock
    private DefaultDeliveryPolicyService defaultDeliveryPolicyService;

    @Mock
    private DefaultPointPolicyService defaultPointPolicyService;

    @InjectMocks
    private DefaultPolicyServiceImpl defaultPolicyService;

    @Test
    void testGetDefaultPolicies_Success() {
        DefaultDeliveryPolicyDTO deliveryPolicy1 = new DefaultDeliveryPolicyDTO(1L, "Express", 3000, null);
        DefaultDeliveryPolicyDTO deliveryPolicy2 = new DefaultDeliveryPolicyDTO(2L, "Standard", 1500, null);
        List<DefaultDeliveryPolicyDTO> deliveryPolicies = List.of(deliveryPolicy1, deliveryPolicy2);

        DefaultPointPolicyDTO pointPolicy1 = new DefaultPointPolicyDTO(1L, null, 1L, "Point Policy 1", 100, null);
        DefaultPointPolicyDTO pointPolicy2 = new DefaultPointPolicyDTO(2L, null, 2L, "Point Policy 2", 200, null);
        List<DefaultPointPolicyDTO> pointPolicies = List.of(pointPolicy1, pointPolicy2);

        when(defaultDeliveryPolicyService.getDefaultDeliveryDTO()).thenReturn(deliveryPolicies);
        when(defaultPointPolicyService.getDefaultPointPolicies()).thenReturn(pointPolicies);

        DefaultPolicyDTO result = defaultPolicyService.getDefaultPolicies();

        assertNotNull(result);
        assertEquals(2, result.getDeliveryPolicies().size());
        assertEquals(2, result.getPointPolicies().size());
        assertEquals("Express", result.getDeliveryPolicies().get(0).getName());
        assertEquals("Point Policy 1", result.getPointPolicies().get(0).getName());

        verify(defaultDeliveryPolicyService, times(1)).getDefaultDeliveryDTO();
        verify(defaultPointPolicyService, times(1)).getDefaultPointPolicies();
    }

    @Test
    void testGetDefaultPolicies_EmptyLists() {
        when(defaultDeliveryPolicyService.getDefaultDeliveryDTO()).thenReturn(List.of());
        when(defaultPointPolicyService.getDefaultPointPolicies()).thenReturn(List.of());

        DefaultPolicyDTO result = defaultPolicyService.getDefaultPolicies();

        assertNotNull(result);
        assertTrue(result.getDeliveryPolicies().isEmpty());
        assertTrue(result.getPointPolicies().isEmpty());

        verify(defaultDeliveryPolicyService, times(1)).getDefaultDeliveryDTO();
        verify(defaultPointPolicyService, times(1)).getDefaultPointPolicies();
    }
}
