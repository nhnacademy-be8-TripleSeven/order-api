package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.ordergrouppointhistory.OrderGroupPointHistoryRequestDTO;
import com.tripleseven.orderapi.dto.ordergrouppointhistory.OrderGroupPointHistoryResponseDTO;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.ordergrouppointhistory.OrderGroupPointHistory;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.ordergrouppointhistory.OrderGroupPointHistoryRepository;
import com.tripleseven.orderapi.repository.ordergrouppointhistory.querydsl.QueryDslOrderGroupPointHistoryRepository;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.service.ordergrouppointhistory.OrderGroupPointHistoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderGroupPointHistoryServiceTest {

    @Mock
    private OrderGroupPointHistoryRepository orderGroupPointHistoryRepository;

    @Mock
    private QueryDslOrderGroupPointHistoryRepository queryDslOrderGroupPointHistoryRepository;

    @Mock
    private OrderGroupRepository orderGroupRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private OrderGroupPointHistoryServiceImpl service;

    @Test
    void testGetUsedPoint_Success() {
        Long orderGroupId = 1L;
        HistoryTypes historyType = HistoryTypes.SPEND;

        when(queryDslOrderGroupPointHistoryRepository.findTotalAmountByOrderGroupId(orderGroupId, historyType))
                .thenReturn(100L);

        long usedPoint = service.getUsedPoint(orderGroupId);

        assertEquals(100L, usedPoint);
    }

    @Test
    void testGetUsedPoint_NoPoints() {
        Long orderGroupId = 1L;
        HistoryTypes historyType = HistoryTypes.SPEND;

        when(queryDslOrderGroupPointHistoryRepository.findTotalAmountByOrderGroupId(orderGroupId, historyType))
                .thenReturn(null);

        long usedPoint = service.getUsedPoint(orderGroupId);

        assertEquals(0, usedPoint);
    }

    @Test
    void testGetEarnedPoint_Success() {
        Long orderGroupId = 1L;
        HistoryTypes historyType = HistoryTypes.EARN;

        when(queryDslOrderGroupPointHistoryRepository.findTotalAmountByOrderGroupId(orderGroupId, historyType))
                .thenReturn(200L);

        long earnedPoint = service.getEarnedPoint(orderGroupId);

        assertEquals(200L, earnedPoint);
    }

    @Test
    void testCreateOrderGroupPointHistory_Success() {
        Long orderGroupId = 1L;
        Long pointHistoryId = 2L;

        OrderGroup orderGroup = new OrderGroup();
        ReflectionTestUtils.setField(orderGroup, "id", 1L);

        PointHistory pointHistory = new PointHistory();
        ReflectionTestUtils.setField(pointHistory, "id", 1L);

        OrderGroupPointHistoryRequestDTO request = new OrderGroupPointHistoryRequestDTO(orderGroupId, pointHistoryId);
        OrderGroupPointHistory savedOrderGroupPointHistory = new OrderGroupPointHistory();
        savedOrderGroupPointHistory.ofCreate(pointHistory,orderGroup);
        ReflectionTestUtils.setField(savedOrderGroupPointHistory, "id", 1L);

        when(orderGroupRepository.findById(orderGroupId)).thenReturn(Optional.of(orderGroup));
        when(pointHistoryRepository.findById(pointHistoryId)).thenReturn(Optional.of(pointHistory));
        when(orderGroupPointHistoryRepository.save(any(OrderGroupPointHistory.class))).thenReturn(savedOrderGroupPointHistory);

        OrderGroupPointHistoryResponseDTO response = service.createOrderGroupPointHistory(request);

        assertNotNull(response);
        verify(orderGroupRepository, times(1)).findById(orderGroupId);
        verify(pointHistoryRepository, times(1)).findById(pointHistoryId);
        verify(orderGroupPointHistoryRepository, times(1)).save(any(OrderGroupPointHistory.class));
    }

    @Test
    void testCreateOrderGroupPointHistory_OrderGroupNotFound() {
        Long orderGroupId = 1L;
        Long pointHistoryId = 2L;

        OrderGroupPointHistoryRequestDTO request = new OrderGroupPointHistoryRequestDTO(orderGroupId, pointHistoryId);

        when(orderGroupRepository.findById(orderGroupId)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> service.createOrderGroupPointHistory(request));
        verify(orderGroupRepository, times(1)).findById(orderGroupId);
        verify(pointHistoryRepository, never()).findById(anyLong());
        verify(orderGroupPointHistoryRepository, never()).save(any(OrderGroupPointHistory.class));
    }

    @Test
    void testCreateOrderGroupPointHistory_PointHistoryNotFound() {
        Long orderGroupId = 1L;
        Long pointHistoryId = 2L;

        OrderGroup orderGroup = new OrderGroup();
        OrderGroupPointHistoryRequestDTO request = new OrderGroupPointHistoryRequestDTO(orderGroupId, pointHistoryId);

        when(orderGroupRepository.findById(orderGroupId)).thenReturn(Optional.of(orderGroup));
        when(pointHistoryRepository.findById(pointHistoryId)).thenReturn(Optional.empty());

        // 예외 검증
        assertThrows(CustomException.class, () -> service.createOrderGroupPointHistory(request));
        verify(orderGroupRepository, times(1)).findById(orderGroupId);
        verify(pointHistoryRepository, times(1)).findById(pointHistoryId);
        verify(orderGroupPointHistoryRepository, never()).save(any(OrderGroupPointHistory.class));
    }
}
