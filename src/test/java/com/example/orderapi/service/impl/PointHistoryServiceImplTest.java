package com.example.orderapi.service.impl;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
import com.example.orderapi.entity.pointhistory.HistoryTypes;
import com.example.orderapi.entity.pointhistory.PointHistory;
import com.example.orderapi.entity.pointpolicy.PointPolicy;
import com.example.orderapi.exception.notfound.impl.PointHistoryNotFoundException;
import com.example.orderapi.exception.notfound.impl.PointPolicyNotFoundException;
import com.example.orderapi.repository.pointhistory.PointHistoryRepository;
import com.example.orderapi.repository.pointpolicy.PointPolicyRepository;
import com.example.orderapi.service.pointhistory.PointHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointHistoryServiceImplTest {

    @InjectMocks
    private PointHistoryServiceImpl pointHistoryService;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMemberPointHistory_shouldReturnPageOfHistories() {
        // Arrange
        Long memberId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);
        PointHistory history = new PointHistory(1L, HistoryTypes.EARN, 100, LocalDateTime.now(), "Comment", memberId);
        Page<PointHistory> mockPage = new PageImpl<>(List.of(history));

        when(pointHistoryRepository.findAllByMemberId(memberId, pageable)).thenReturn(mockPage);

        // Act
        Page<PointHistoryResponse> result = pointHistoryService.getMemberPointHistory(memberId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Comment", result.getContent().get(0).getComment());
        verify(pointHistoryRepository, times(1)).findAllByMemberId(memberId, pageable);
    }

    @Test
    void getMemberPointHistory_shouldThrowNotFoundException_whenHistoriesAreNull() {
        // Arrange
        Long memberId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);
        when(pointHistoryRepository.findAllByMemberId(memberId, pageable)).thenReturn(Page.empty());

        // Act & Assert
        assertThrows(PointHistoryNotFoundException.class, () -> pointHistoryService.getMemberPointHistory(memberId, pageable));
        verify(pointHistoryRepository, times(1)).findAllByMemberId(memberId, pageable);
    }

    @Test
    void assignPointBasedOnPolicy_shouldSaveHistory() {
        // Arrange
        Long policyId = 1L;
        Long memberId = 1L;
        PointPolicy mockPolicy = new PointPolicy(policyId, "Test Policy", 100, BigDecimal.valueOf(0.1));
        when(pointPolicyRepository.findById(policyId)).thenReturn(Optional.of(mockPolicy));

        // Act
        PointHistoryResponse result = pointHistoryService.assignPointBasedOnPolicy(policyId, memberId);

        // Assert
        assertNotNull(result);
        assertEquals("Test Policy", result.getComment());
        assertEquals(100, result.getAmount());
        verify(pointPolicyRepository, times(1)).findById(policyId);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    void assignPointBasedOnPolicy_shouldThrowException_whenPolicyNotFound() {
        // Arrange
        Long policyId = 1L;
        Long memberId = 1L;
        when(pointPolicyRepository.findById(policyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PointPolicyNotFoundException.class, () -> pointHistoryService.assignPointBasedOnPolicy(policyId, memberId));
        verify(pointPolicyRepository, times(1)).findById(policyId);
        verify(pointHistoryRepository, never()).save(any(PointHistory.class));
    }

    @Test
    void calculateTotalPoints_shouldReturnSum() {
        // Arrange
        Long pointId = 1L;
        int sum = 500;
        when(pointHistoryRepository.sumAmount(pointId)).thenReturn(sum);

        // Act
        Integer result = pointHistoryService.calculateTotalPoints(pointId);

        // Assert
        assertEquals(sum, result);
        verify(pointHistoryRepository, times(1)).sumAmount(pointId);
    }

    @Test
    void createPointHistory_shouldSaveAndReturnResponse() {
        // Arrange
        PointHistoryCreateRequest request = new PointHistoryCreateRequest(1L, HistoryTypes.EARN, 100, LocalDateTime.now(), "Test Comment");
        PointHistory savedHistory = new PointHistory(1L, request.getTypes(), request.getAmount(), request.getChanged_at(), request.getComment(), request.getMemberId());

        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(savedHistory);

        // Act
        PointHistoryResponse result = pointHistoryService.createPointHistory(request);

        // Assert
        assertNotNull(result);
        assertEquals("Test Comment", result.getComment());
        assertEquals(100, result.getAmount());
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    void removePointHistoryById_shouldInvokeRepositoryDelete() {
        // Arrange
        Long pointHistoryId = 1L;

        // Act
        pointHistoryService.removePointHistoryById(pointHistoryId);

        // Assert
        verify(pointHistoryRepository, times(1)).deleteById(pointHistoryId);
    }

    @Test
    void removeAllPointHistoriesForMember_shouldInvokeRepositoryDeleteAll() {
        // Arrange
        Long memberId = 1L;

        // Act
        pointHistoryService.removeAllPointHistoriesForMember(memberId);

        // Assert
        verify(pointHistoryRepository, times(1)).deleteAllByMemberId(memberId);
    }
}