package com.example.orderapi.service.impl;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
import com.example.orderapi.entity.PointHistory.HistoryTypes;
import com.example.orderapi.entity.PointHistory.PointHistory;
import com.example.orderapi.exception.notfound.impl.PointHistoryNotFoundException;
import com.example.orderapi.repository.PointHistoryRepository;
import com.example.orderapi.repository.PointPolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceImplTest {

    @InjectMocks
    private PointHistoryServiceImpl pointHistoryService;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @Test
    void testFindByMemberId() {
        // Given
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        Page<PointHistory> mockPage = new PageImpl<>(List.of(
                new PointHistory(1L, HistoryTypes.EARN, 500, LocalDateTime.now(), "Bonus", memberId)
        ));

        when(pointHistoryRepository.findAllByMemberId(memberId, pageable)).thenReturn(mockPage);

        // When
        Page<PointHistoryResponse> result = pointHistoryService.findByMemberId(memberId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(pointHistoryRepository, times(1)).findAllByMemberId(memberId, pageable);
    }

    @Test
    void testFindByMemberId_NotFound(){
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 5);

        doThrow(new PointHistoryNotFoundException("memberId=" + memberId + "pointHistoryNotFoundException")).when(pointHistoryRepository).findAllByMemberId(memberId, pageable);

        assertThrows(PointHistoryNotFoundException.class, () -> pointHistoryService.findByMemberId(memberId, pageable));

        verify(pointHistoryRepository, times(1)).findAllByMemberId(memberId, pageable);
    }

    @Test
    void findAll() {
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        Page<PointHistory> mockPage = new PageImpl<>(List.of(
                new PointHistory(1L, HistoryTypes.EARN, 500, LocalDateTime.now(), "Bonus", memberId)
        ));

        when(pointHistoryRepository.findAll(pageable)).thenReturn(mockPage);

        Page<PointHistory> result = pointHistoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(pointHistoryRepository, times(1)).findAll(pageable);

    }

    @Test
    void testDeleteByPointHistoryId() {
        // Given
        Long pointHistoryId = 1L;

        // When
        pointHistoryService.deleteByPointHistoryId(pointHistoryId);

        // Then
        // Verify that the repository's deleteById method was called once with the given ID
        verify(pointHistoryRepository, times(1)).deleteById(pointHistoryId);
    }


    @Test
    void deleteByMemberId() {
        //Given
        Long memberId = 1L;
        pointHistoryService.deleteByMemberId(memberId);

        verify(pointHistoryRepository, times(1)).deleteAllByMemberId(memberId);
    }

    @Test
    void findByPointHistoryId() {
        //given
        Long pointHistoryId = 1L;
        PointHistory pointHistory = new PointHistory();
        pointHistory.setMemberId(pointHistoryId);
        pointHistory.setComment("Review Event");
        pointHistory.setTypes(HistoryTypes.EARN);
        pointHistory.setAmount(500);

        when(pointHistoryRepository.findById(pointHistoryId)).thenReturn(Optional.of(pointHistory));

        PointHistoryResponse result = pointHistoryService.findByPointHistoryId(pointHistoryId);
        assertNotNull(result);
        verify(pointHistoryRepository, times(1)).findById(pointHistoryId);

    }

    @Test
    void save() {
        PointHistoryCreateRequest request = new PointHistoryCreateRequest();
    }

    @Test
    void testSave() {
    }
}