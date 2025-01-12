package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyCreateRequestDTO;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyResponseDTO;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.notfound.PointPolicyNotFoundException;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
import com.tripleseven.orderapi.service.pointpolicy.PointPolicyServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointPolicyServiceTest {

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @InjectMocks
    private PointPolicyServiceImpl pointPolicyService;

    @Test
    void testFindById_Success() {
        Long id = 1L;
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("Policy 1", 100, BigDecimal.ONE);
        ReflectionTestUtils.setField(pointPolicy, "id", id);

        when(pointPolicyRepository.findById(id)).thenReturn(Optional.of(pointPolicy));

        PointPolicyResponseDTO result = pointPolicyService.findById(id);

        assertNotNull(result);
        assertEquals("Policy 1", result.getName());
        assertEquals(100, result.getAmount());
        assertEquals(0, BigDecimal.ONE.compareTo(result.getRate()));

        verify(pointPolicyRepository, times(1)).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        Long id = 1L;
        when(pointPolicyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PointPolicyNotFoundException.class, () -> pointPolicyService.findById(id));

        verify(pointPolicyRepository, times(1)).findById(id);
    }

    @Test
    void testSave_Success() {
        PointPolicyCreateRequestDTO request = new PointPolicyCreateRequestDTO("Policy 1", 100, BigDecimal.ONE);
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate(request.getName(), request.getAmount(), request.getRate());
        ReflectionTestUtils.setField(pointPolicy, "id", 1L);

        when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(pointPolicy);

        PointPolicyResponseDTO result = pointPolicyService.save(request);

        assertNotNull(result);
        assertEquals("Policy 1", result.getName());
        assertEquals(100, result.getAmount());
        assertEquals(0, BigDecimal.ONE.compareTo(result.getRate()));

        verify(pointPolicyRepository, times(1)).save(any(PointPolicy.class));
    }

    @Test
    void testUpdate_Success() {
        Long id = 1L;
        PointPolicyUpdateRequestDTO request = new PointPolicyUpdateRequestDTO("Updated Policy", 200, BigDecimal.TEN);
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("Policy 1", 100, BigDecimal.ONE);
        ReflectionTestUtils.setField(pointPolicy, "id", id);

        when(pointPolicyRepository.findById(id)).thenReturn(Optional.of(pointPolicy));
        when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(pointPolicy);

        PointPolicyResponseDTO result = pointPolicyService.update(id, request);

        assertNotNull(result);
        assertEquals("Updated Policy", result.getName());
        assertEquals(200, result.getAmount());
        assertEquals(0, BigDecimal.TEN.compareTo(result.getRate()));

        verify(pointPolicyRepository, times(1)).findById(id);
        verify(pointPolicyRepository, times(1)).save(any(PointPolicy.class));
    }

    @Test
    void testUpdate_NotFound() {
        Long id = 1L;
        PointPolicyUpdateRequestDTO request = new PointPolicyUpdateRequestDTO("Updated Policy", 200, BigDecimal.TEN);

        when(pointPolicyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PointPolicyNotFoundException.class, () -> pointPolicyService.update(id, request));

        verify(pointPolicyRepository, times(1)).findById(id);
    }

    @Test
    void testDelete_Success() {
        Long id = 1L;

        when(pointPolicyRepository.existsById(id)).thenReturn(true);

        pointPolicyService.delete(id);

        verify(pointPolicyRepository, times(1)).existsById(id);
        verify(pointPolicyRepository, times(1)).deleteById(id);
    }

    @Test
    void testDelete_NotFound() {
        Long id = 1L;

        when(pointPolicyRepository.existsById(id)).thenReturn(false);

        assertThrows(PointPolicyNotFoundException.class, () -> pointPolicyService.delete(id));

        verify(pointPolicyRepository, times(1)).existsById(id);
        verify(pointPolicyRepository, never()).deleteById(id);
    }

    @Test
    void testFindAll_Success() {
        PointPolicy policy1 = new PointPolicy();
        policy1.ofCreate("Policy 1", 100, BigDecimal.ONE);
        ReflectionTestUtils.setField(policy1, "id", 1L);

        PointPolicy policy2 = new PointPolicy();
        policy2.ofCreate("Policy 2", 200, BigDecimal.TEN);
        ReflectionTestUtils.setField(policy2, "id", 2L);

        when(pointPolicyRepository.findAll()).thenReturn(List.of(policy1, policy2));

        List<PointPolicyResponseDTO> result = pointPolicyService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Policy 1", result.get(0).getName());
        assertEquals("Policy 2", result.get(1).getName());

        verify(pointPolicyRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_Empty() {
        when(pointPolicyRepository.findAll()).thenReturn(List.of());

        List<PointPolicyResponseDTO> result = pointPolicyService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(pointPolicyRepository, times(1)).findAll();
    }
}
