package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryCreateRequestDTO;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.notfound.PointHistoryNotFoundException;
import com.tripleseven.orderapi.exception.notfound.PointPolicyNotFoundException;
import com.tripleseven.orderapi.repository.ordergrouppointhistory.querydsl.QueryDslOrderGroupPointHistoryRepository;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
import com.tripleseven.orderapi.service.pointhistory.PointHistoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @Mock
    private QueryDslOrderGroupPointHistoryRepository queryDslOrderGroupPointHistoryRepository;

    @InjectMocks
    private PointHistoryServiceImpl pointHistoryService;

    @Test
    void testGetPointHistoriesByMemberId_Success() {
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        PointHistory pointHistory = PointHistory.ofCreate(HistoryTypes.EARN, 100, "Earned Points", memberId);
        ReflectionTestUtils.setField(pointHistory, "id", 1L);

        when(pointHistoryRepository.findAllByMemberId(memberId, pageable))
                .thenReturn(new PageImpl<>(List.of(pointHistory)));

        Page<PointHistoryResponseDTO> result = pointHistoryService.getPointHistoriesByMemberId(memberId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(100, result.getContent().get(0).getAmount());
        verify(pointHistoryRepository, times(1)).findAllByMemberId(memberId, pageable);
    }

    @Test
    void testGetPointHistoriesByMemberId_NotFound() {
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(pointHistoryRepository.findAllByMemberId(memberId, pageable))
                .thenReturn(Page.empty());

        assertThrows(PointHistoryNotFoundException.class,
                () -> pointHistoryService.getPointHistoriesByMemberId(memberId, pageable));
        verify(pointHistoryRepository, times(1)).findAllByMemberId(memberId, pageable);
    }

    @Test
    void testGetPointHistoriesWithinPeriod_Success() {
        Long memberId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        String sortDirection = "ASC";
        Pageable pageable = PageRequest.of(0, 10);
        PointHistory pointHistory = PointHistory.ofCreate(HistoryTypes.EARN, 100, "Earned Points", memberId);
        ReflectionTestUtils.setField(pointHistory, "id", 1L);

        when(pointHistoryRepository.findAllByChangedAtBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(pointHistory)));

        Page<PointHistoryResponseDTO> result = pointHistoryService.getPointHistoriesWithinPeriod(
                memberId, startDate, endDate, sortDirection, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(100, result.getContent().get(0).getAmount());
        verify(pointHistoryRepository, times(1))
                .findAllByChangedAtBetween(eq(memberId), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testRemovePointHistoryById_Success() {
        Long pointHistoryId = 1L;

        when(pointHistoryRepository.existsById(pointHistoryId)).thenReturn(true);

        pointHistoryService.removePointHistoryById(pointHistoryId);

        verify(pointHistoryRepository, times(1)).existsById(pointHistoryId);
        verify(pointHistoryRepository, times(1)).deleteById(pointHistoryId);
    }

    @Test
    void testRemovePointHistoryById_NotFound() {
        Long pointHistoryId = 1L;

        when(pointHistoryRepository.existsById(pointHistoryId)).thenReturn(false);

        assertThrows(PointHistoryNotFoundException.class,
                () -> pointHistoryService.removePointHistoryById(pointHistoryId));
        verify(pointHistoryRepository, times(1)).existsById(pointHistoryId);
        verify(pointHistoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void testCreatePointHistory_Success() {
        Long memberId = 1L;
        PointHistoryCreateRequestDTO request = new PointHistoryCreateRequestDTO( HistoryTypes.EARN, 1L, 1L);
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("Earn Policy", 100, BigDecimal.ONE);
        ReflectionTestUtils.setField(pointPolicy, "id", 1L);

        PointHistory pointHistory = PointHistory.ofCreate(
                HistoryTypes.EARN, 100, "Earn Policy", memberId);
        ReflectionTestUtils.setField(pointHistory, "id", 1L);

        when(pointPolicyRepository.findById(1L)).thenReturn(Optional.of(pointPolicy));
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);

        PointHistoryResponseDTO result = pointHistoryService.createPointHistory(memberId, request);

        assertNotNull(result);
        assertEquals(100, result.getAmount());
        assertEquals(HistoryTypes.EARN, result.getTypes());
        verify(pointPolicyRepository, times(1)).findById(1L);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    void testCreatePointHistory_PointPolicyNotFound() {
        Long memberId = 1L;
        PointHistoryCreateRequestDTO request = new PointHistoryCreateRequestDTO(HistoryTypes.EARN, 1L, 1L);

        when(pointPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PointPolicyNotFoundException.class,
                () -> pointHistoryService.createPointHistory(memberId, request));
        verify(pointPolicyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTotalPointByMemberId_Success() {
        Long memberId = 1L;
        when(pointHistoryRepository.sumAmount(memberId)).thenReturn(500);

        Integer result = pointHistoryService.getTotalPointByMemberId(memberId);

        assertNotNull(result);
        assertEquals(500, (int) result);
        verify(pointHistoryRepository, times(1)).sumAmount(memberId);
    }
}
