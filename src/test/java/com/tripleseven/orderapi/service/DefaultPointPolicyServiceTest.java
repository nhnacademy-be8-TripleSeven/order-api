package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.defaultpointpolicy.DefaultPointPolicy;
import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.notfound.PointHistoryNotFoundException;
import com.tripleseven.orderapi.repository.defaultpointpolicy.DefaultPointPolicyRepository;
import com.tripleseven.orderapi.repository.defaultpointpolicy.querydsl.QueryDslDefaultPointPolicyRepository;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
import com.tripleseven.orderapi.service.defaultpointpolicy.DefaultPointPolicyServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultPointPolicyServiceTest {

    @Mock
    private QueryDslDefaultPointPolicyRepository queryDslDefaultPointPolicyRepository;

    @Mock
    private DefaultPointPolicyRepository defaultPointPolicyRepository;

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @InjectMocks
    private DefaultPointPolicyServiceImpl defaultPointPolicyService;

    @Test
    void testGetDefaultPointPolicyDTO_Success() {
        PointPolicyType type = PointPolicyType.DEFAULT_BUY;
        DefaultPointPolicyDTO policyDTO = new DefaultPointPolicyDTO(1L, type, 2L, "Test Policy", 100, BigDecimal.ZERO);

        when(queryDslDefaultPointPolicyRepository.findDefaultPointPolicyByType(type)).thenReturn(policyDTO);

        DefaultPointPolicyDTO result = defaultPointPolicyService.getDefaultPointPolicyDTO(type);

        assertNotNull(result);
        assertEquals(1L, (long) result.getId());
        assertEquals(type, result.getType());
        assertEquals(2L, (long) result.getPointPolicyId());
        assertEquals("Test Policy", result.getName());
        assertEquals(100, result.getAmount());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getRate()));

        verify(queryDslDefaultPointPolicyRepository, times(1)).findDefaultPointPolicyByType(type);
    }

    @Test
    void testGetDefaultPointPolicies_Success() {
        DefaultPointPolicyDTO policy1 = new DefaultPointPolicyDTO(1L, PointPolicyType.DEFAULT_BUY, 2L, "Policy 1", 100, BigDecimal.ZERO);
        DefaultPointPolicyDTO policy2 = new DefaultPointPolicyDTO(2L, PointPolicyType.CONTENT_REVIEW, 3L, "Policy 2", 200, BigDecimal.ZERO);
        List<DefaultPointPolicyDTO> policies = List.of(policy1, policy2);

        when(queryDslDefaultPointPolicyRepository.findDefaultPointPolicies()).thenReturn(policies);

        List<DefaultPointPolicyDTO> result = defaultPointPolicyService.getDefaultPointPolicies();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Policy 1", result.get(0).getName());
        assertEquals("Policy 2", result.get(1).getName());

        verify(queryDslDefaultPointPolicyRepository, times(1)).findDefaultPointPolicies();
    }

    @Test
    void testGetDefaultPointPolicies_EmptyList() {
        when(queryDslDefaultPointPolicyRepository.findDefaultPointPolicies()).thenReturn(null);

        List<DefaultPointPolicyDTO> result = defaultPointPolicyService.getDefaultPointPolicies();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(queryDslDefaultPointPolicyRepository, times(1)).findDefaultPointPolicies();
    }

    @Test
    void testUpdateDefaultPoint_CreatePolicy_Success() {
        PointPolicyType type = PointPolicyType.DEFAULT_BUY;
        DefaultPointPolicyUpdateRequestDTO request = new DefaultPointPolicyUpdateRequestDTO(2L, type);

        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("Test Policy", 1000, BigDecimal.ZERO);
        ReflectionTestUtils.setField(pointPolicy, "id", 2L);

        DefaultPointPolicy defaultPointPolicy = new DefaultPointPolicy();
        ReflectionTestUtils.setField(defaultPointPolicy, "id", 1L);

        when(defaultPointPolicyRepository.findDefaultPointPolicyByPointPolicyType(type)).thenReturn(null);
        when(pointPolicyRepository.findById(2L)).thenReturn(Optional.of(pointPolicy));
        when(defaultPointPolicyRepository.save(any())).thenReturn(defaultPointPolicy);
        Long result = defaultPointPolicyService.updateDefaultPoint(request);

        assertNotNull(result);
        verify(defaultPointPolicyRepository, times(1)).findDefaultPointPolicyByPointPolicyType(type);
        verify(pointPolicyRepository, times(1)).findById(2L);
    }

    @Test
    void testUpdateDefaultPoint_UpdatePolicy_Success() {
        PointPolicyType type = PointPolicyType.DEFAULT_BUY;
        DefaultPointPolicyUpdateRequestDTO request = new DefaultPointPolicyUpdateRequestDTO(2L, type);

        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("Update Policy", 1000, BigDecimal.ZERO);

        DefaultPointPolicy defaultPointPolicy = new DefaultPointPolicy();
        defaultPointPolicy.ofCreate(type, pointPolicy);
        ReflectionTestUtils.setField(defaultPointPolicy, "id", 1L);

        when(defaultPointPolicyRepository.findDefaultPointPolicyByPointPolicyType(type)).thenReturn(defaultPointPolicy);
        when(pointPolicyRepository.findById(2L)).thenReturn(Optional.of(pointPolicy));

        Long result = defaultPointPolicyService.updateDefaultPoint(request);

        assertNotNull(result);
        verify(defaultPointPolicyRepository, times(1)).findDefaultPointPolicyByPointPolicyType(type);
        verify(pointPolicyRepository, times(1)).findById(2L);
    }

    @Test
    void testUpdateDefaultPoint_PointPolicyNotFound() {
        // Mock 데이터 준비
        PointPolicyType type = PointPolicyType.DEFAULT_BUY;
        DefaultPointPolicyUpdateRequestDTO request = new DefaultPointPolicyUpdateRequestDTO(3L, type);

        // Mock 설정
        when(defaultPointPolicyRepository.findDefaultPointPolicyByPointPolicyType(type)).thenReturn(null);
        when(pointPolicyRepository.findById(3L)).thenReturn(Optional.empty());

        // 예외 검증
        assertThrows(PointHistoryNotFoundException.class, () -> defaultPointPolicyService.updateDefaultPoint(request));
        verify(defaultPointPolicyRepository, times(1)).findDefaultPointPolicyByPointPolicyType(type);
        verify(pointPolicyRepository, times(1)).findById(3L);
    }
}