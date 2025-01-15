package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DefaultDeliveryPolicy;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;
import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.repository.defaultdeliverypolicy.DefaultDeliveryPolicyRepository;
import com.tripleseven.orderapi.repository.defaultdeliverypolicy.querydsl.QueryDslDefaultDeliveryPolicyRepository;
import com.tripleseven.orderapi.repository.deliverypolicy.DeliveryPolicyRepository;
import com.tripleseven.orderapi.service.defaultdeliverypolicy.DefaultDeliveryPolicyServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultDeliveryPolicyServiceTest {

    @Mock
    private QueryDslDefaultDeliveryPolicyRepository queryDslDeliveryPolicyRepository;

    @Mock
    private DefaultDeliveryPolicyRepository defaultDeliveryPolicyRepository;

    @Mock
    private DeliveryPolicyRepository deliveryPolicyRepository;

    @InjectMocks
    private DefaultDeliveryPolicyServiceImpl defaultDeliveryPolicyService;

    @Test
    void testGetDefaultDeliveryDTO_Success() {
        DefaultDeliveryPolicyDTO policy1 = new DefaultDeliveryPolicyDTO(1L, "Policy 1", 10000, 1000, DeliveryPolicyType.DEFAULT);
        DefaultDeliveryPolicyDTO policy2 = new DefaultDeliveryPolicyDTO(2L, "Policy 2", 20000, 2000, DeliveryPolicyType.EVENT);
        List<DefaultDeliveryPolicyDTO> policies = List.of(policy1, policy2);

        when(queryDslDeliveryPolicyRepository.findDefaultDeliveryPolicy()).thenReturn(policies);

        List<DefaultDeliveryPolicyDTO> result = defaultDeliveryPolicyService.getDefaultDeliveryDTO();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Policy 1", result.get(0).getName());
        assertEquals(1000, (int) result.get(0).getPrice());

        verify(queryDslDeliveryPolicyRepository, times(1)).findDefaultDeliveryPolicy();
    }

    @Test
    void testUpdateDefaultDelivery_CreatePolicy_Success() {
        DeliveryPolicyType type = DeliveryPolicyType.DEFAULT;
        DefaultDeliveryPolicyUpdateRequestDTO request = new DefaultDeliveryPolicyUpdateRequestDTO(2L, type);

        DeliveryPolicy deliveryPolicy = new DeliveryPolicy();
        deliveryPolicy.ofCreate("Standard Delivery", 10000, 1000);
        ReflectionTestUtils.setField(deliveryPolicy, "id", 2L);

        DefaultDeliveryPolicy defaultDeliveryPolicy = new DefaultDeliveryPolicy();
        defaultDeliveryPolicy.ofCreate(type, deliveryPolicy);

        when(defaultDeliveryPolicyRepository.findDefaultDeliveryPolicyByDeliveryPolicyType(type)).thenReturn(null);
        when(deliveryPolicyRepository.findById(2L)).thenReturn(Optional.of(deliveryPolicy));
        when(defaultDeliveryPolicyRepository.save(any(DefaultDeliveryPolicy.class))).thenAnswer(invocation -> {
            DefaultDeliveryPolicy savedPolicy = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedPolicy, "id", 1L);
            return savedPolicy;
        });
        Long result = defaultDeliveryPolicyService.updateDefaultDelivery(request);

        assertNotNull(result);
        assertEquals(1L, (long) result);

        verify(defaultDeliveryPolicyRepository, times(1)).findDefaultDeliveryPolicyByDeliveryPolicyType(type);
        verify(deliveryPolicyRepository, times(1)).findById(2L);
        verify(defaultDeliveryPolicyRepository, times(1)).save(any(DefaultDeliveryPolicy.class));
    }

    @Test
    void testUpdateDefaultDelivery_UpdatePolicy_Success() {
        DeliveryPolicyType type = DeliveryPolicyType.DEFAULT;
        DefaultDeliveryPolicyUpdateRequestDTO request = new DefaultDeliveryPolicyUpdateRequestDTO(3L, type);

        DeliveryPolicy deliveryPolicy = new DeliveryPolicy();
        deliveryPolicy.ofCreate("Updated Delivery", 20000, 2000);
        ReflectionTestUtils.setField(deliveryPolicy, "id", 3L);

        DefaultDeliveryPolicy defaultDeliveryPolicy = new DefaultDeliveryPolicy();
        defaultDeliveryPolicy.ofCreate(type, deliveryPolicy);
        ReflectionTestUtils.setField(defaultDeliveryPolicy, "id", 1L);

        when(defaultDeliveryPolicyRepository.findDefaultDeliveryPolicyByDeliveryPolicyType(type)).thenReturn(defaultDeliveryPolicy);
        when(deliveryPolicyRepository.findById(3L)).thenReturn(Optional.of(deliveryPolicy));

        Long result = defaultDeliveryPolicyService.updateDefaultDelivery(request);

        assertNotNull(result);
        assertEquals(defaultDeliveryPolicy.getId(), result);

        verify(defaultDeliveryPolicyRepository, times(1)).findDefaultDeliveryPolicyByDeliveryPolicyType(type);
        verify(deliveryPolicyRepository, times(1)).findById(3L);
    }

    @Test
    void testUpdateDefaultDelivery_DeliveryPolicyNotFound() {
        DeliveryPolicyType type = DeliveryPolicyType.DEFAULT;
        DefaultDeliveryPolicyUpdateRequestDTO request = new DefaultDeliveryPolicyUpdateRequestDTO(4L, type);

        when(defaultDeliveryPolicyRepository.findDefaultDeliveryPolicyByDeliveryPolicyType(type)).thenReturn(null);
        when(deliveryPolicyRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> defaultDeliveryPolicyService.updateDefaultDelivery(request));

        verify(defaultDeliveryPolicyRepository, times(1)).findDefaultDeliveryPolicyByDeliveryPolicyType(type);
        verify(deliveryPolicyRepository, times(1)).findById(4L);
        verify(defaultDeliveryPolicyRepository, never()).save(any(DefaultDeliveryPolicy.class));
    }

    @Test
    void testGetDefaultDeliveryPolicy_Success() {
        DeliveryPolicyType type = DeliveryPolicyType.EVENT;
        DeliveryPolicy deliveryPolicy = new DeliveryPolicy();
        deliveryPolicy.ofCreate("Express Delivery", 30000, 3000);
        ReflectionTestUtils.setField(deliveryPolicy, "id", 2L);

        DefaultDeliveryPolicy defaultDeliveryPolicy = new DefaultDeliveryPolicy();
        defaultDeliveryPolicy.ofCreate(type, deliveryPolicy);
        ReflectionTestUtils.setField(defaultDeliveryPolicy, "id", 2L);
        when(defaultDeliveryPolicyRepository.findDefaultDeliveryPolicyByDeliveryPolicyType(type))
                .thenReturn(defaultDeliveryPolicy);

        DefaultDeliveryPolicyDTO result = defaultDeliveryPolicyService.getDefaultDeliveryPolicy(type);

        assertNotNull(result);
        assertEquals(2L, (long) result.getId());
        assertEquals("Express Delivery", result.getName());
        assertEquals(3000, (long) result.getPrice());
        assertEquals(type, result.getType());

        verify(defaultDeliveryPolicyRepository, times(1)).findDefaultDeliveryPolicyByDeliveryPolicyType(type);
    }
}
