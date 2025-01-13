package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryCreateRequestDTO;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryPageResponseDTO;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;
import com.tripleseven.orderapi.dto.pointhistory.UserPointHistoryDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        PointHistoryCreateRequestDTO request = new PointHistoryCreateRequestDTO(HistoryTypes.EARN, 1L, 1L);
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

    @Test
    void testGetPointHistories_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        PointHistory pointHistory = PointHistory.ofCreate(HistoryTypes.SPEND, -50, "Spent Points", 1L);
        ReflectionTestUtils.setField(pointHistory, "id", 2L);

        when(pointHistoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(pointHistory)));

        Page<PointHistoryResponseDTO> result = pointHistoryService.getPointHistories(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(-50, result.getContent().get(0).getAmount());
        assertEquals("Spent Points", result.getContent().get(0).getComment());
        verify(pointHistoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetPointHistories_NotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        when(pointHistoryRepository.findAll(pageable)).thenReturn(Page.empty());

        assertThrows(PointHistoryNotFoundException.class,
                () -> pointHistoryService.getPointHistories(pageable));
        verify(pointHistoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void testRemovePointHistoriesByMemberId_Success() {
        Long memberId = 1L;

        // 실제 삭제 동작을 테스트하지 않으므로 Mock 호출만 검증
        doNothing().when(pointHistoryRepository).deleteAllByMemberId(memberId);

        pointHistoryService.removePointHistoriesByMemberId(memberId);

        verify(pointHistoryRepository, times(1)).deleteAllByMemberId(memberId);
    }

    @Test
    void testGetPointHistoriesWithState_Success() {
        Long memberId = 1L;
        HistoryTypes state = HistoryTypes.EARN;
        Pageable pageable = PageRequest.of(0, 10);
        PointHistory pointHistory = PointHistory.ofCreate(state, 100, "Earned Points", memberId);
        ReflectionTestUtils.setField(pointHistory, "id", 3L);

        when(pointHistoryRepository.findAllByMemberIdAndTypes(memberId, state, pageable))
                .thenReturn(new PageImpl<>(List.of(pointHistory)));

        Page<PointHistoryResponseDTO> result = pointHistoryService.getPointHistoriesWithState(memberId, state, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(100, result.getContent().get(0).getAmount());
        assertEquals("Earned Points", result.getContent().get(0).getComment());
        verify(pointHistoryRepository, times(1)).findAllByMemberIdAndTypes(memberId, state, pageable);
    }

    @Test
    void testGetPointHistoriesWithState_NotFound() {
        Long memberId = 1L;
        HistoryTypes state = HistoryTypes.SPEND;
        Pageable pageable = PageRequest.of(0, 10);

        when(pointHistoryRepository.findAllByMemberIdAndTypes(memberId, state, pageable))
                .thenReturn(Page.empty());

        assertThrows(PointHistoryNotFoundException.class,
                () -> pointHistoryService.getPointHistoriesWithState(memberId, state, pageable));
        verify(pointHistoryRepository, times(1)).findAllByMemberIdAndTypes(memberId, state, pageable);
    }

    @Test
    void testGetUserPointHistories_Success() {
        Long memberId = 1L;
        String startDate = "2023-01-01";
        String endDate = "2023-12-31";
        Pageable pageable = PageRequest.of(0, 10);
        UserPointHistoryDTO userPointHistory = new UserPointHistoryDTO(
                1L,
                100,
                LocalDateTime.now(),
                HistoryTypes.EARN,
                "Earn Points",
                1L);

        when(queryDslOrderGroupPointHistoryRepository.findUserPointHistories(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(userPointHistory)));

        PointHistoryPageResponseDTO<UserPointHistoryDTO> result = pointHistoryService.getUserPointHistories(memberId, startDate, endDate, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Earn Points", result.getContent().get(0).getComment());
        assertEquals(100, result.getContent().get(0).getAmount());
        verify(queryDslOrderGroupPointHistoryRepository, times(1))
                .findUserPointHistories(eq(memberId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void testGetUserPointHistories_EmptyResult() {
        Long memberId = 1L;
        String startDate = "2023-01-01";
        String endDate = "2023-12-31";
        Pageable pageable = PageRequest.of(0, 10);

        when(queryDslOrderGroupPointHistoryRepository.findUserPointHistories(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        PointHistoryPageResponseDTO<UserPointHistoryDTO> result = pointHistoryService.getUserPointHistories(memberId, startDate, endDate, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(queryDslOrderGroupPointHistoryRepository, times(1))
                .findUserPointHistories(eq(memberId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable));
    }
}
