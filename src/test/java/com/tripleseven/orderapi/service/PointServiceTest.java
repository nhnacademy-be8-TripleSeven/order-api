package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.business.point.PointServiceImpl;
import com.tripleseven.orderapi.client.MemberApiClient;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;
import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.repository.defaultpointpolicy.DefaultPointPolicyRepository;
import com.tripleseven.orderapi.repository.defaultpointpolicy.querydsl.QueryDslDefaultPointPolicyRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.ordergrouppointhistory.OrderGroupPointHistoryRepository;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private OrderGroupRepository orderGroupRepository;

    @Mock
    private QueryDslDefaultPointPolicyRepository queryDslDefaultPointPolicyRepository;

    @Mock
    private OrderGroupPointHistoryRepository orderGroupPointHistoryRepository;

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @Mock
    private DefaultPointPolicyRepository defaultPointPolicyRepository;

    @Mock
    private MemberApiClient memberApiClient;

    @InjectMocks
    private PointServiceImpl pointService;

    @Test
    void testCreatePointHistoryForPaymentSpend_Success() {
        Long memberId = 1L;
        Long orderGroupId = 10L;
        int usePoint = 100;

        PointHistory pointHistory = new PointHistory(HistoryTypes.SPEND, -usePoint, LocalDateTime.now(), "포인트 사용", memberId);
        ReflectionTestUtils.setField(pointHistory, "id", 100L);

        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);

        PointHistoryResponseDTO result = pointService.createPointHistoryForPaymentSpend(memberId, usePoint, orderGroupId);

        assertNotNull(result);
        assertEquals(-usePoint, result.getAmount());
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    void testCreatePointHistoryForPaymentEarn_Success() {
        Long memberId = 1L;
        Long orderGroupId = 10L;
        int usedMoney = 5000;

        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("Default Policy", 0, new BigDecimal("0.02"));
        ReflectionTestUtils.setField(pointPolicy, "id", 1L);

        DefaultPointPolicyDTO policyDTO = new DefaultPointPolicyDTO(1L, PointPolicyType.DEFAULT_BUY, pointPolicy);
        when(queryDslDefaultPointPolicyRepository.findDefaultPointPolicyByType(PointPolicyType.DEFAULT_BUY)).thenReturn(policyDTO);

        PointHistory pointHistory = new PointHistory(HistoryTypes.EARN, 100, LocalDateTime.now(), "Default Policy", memberId);
        ReflectionTestUtils.setField(pointHistory, "id", 100L);

        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);
        when(orderGroupRepository.findById(orderGroupId)).thenReturn(Optional.of(new OrderGroup()));
        when(memberApiClient.getGradePoint(anyLong())).thenReturn(100);
        PointHistoryResponseDTO result = pointService.createPointHistoryForPaymentEarn(memberId, usedMoney, orderGroupId);

        assertNotNull(result);
        assertEquals(100, result.getAmount());
        verify(pointHistoryRepository, times(2)).save(any(PointHistory.class));
        verify(orderGroupRepository, times(1)).findById(orderGroupId);
    }

    @Test
    void testCreateRegisterPointHistory_Success() {
        Long memberId = 1L;

        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("회원 가입 정책", 5000, BigDecimal.ZERO);
        ReflectionTestUtils.setField(pointPolicy, "id", 1L);

        DefaultPointPolicyDTO policyDTO = new DefaultPointPolicyDTO(1L, PointPolicyType.REGISTER, pointPolicy);
        when(queryDslDefaultPointPolicyRepository.findDefaultPointPolicyByType(PointPolicyType.REGISTER)).thenReturn(policyDTO);
        when(pointHistoryRepository.findPointHistoryByComment(memberId, "회원 가입 적립")).thenReturn(Optional.empty());

        PointHistory pointHistory = new PointHistory(HistoryTypes.EARN, 5000, LocalDateTime.now(), "회원 가입 적립", memberId);
        ReflectionTestUtils.setField(pointHistory, "id", 1L);

        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);

        PointHistoryResponseDTO result = pointService.createRegisterPointHistory(memberId);

        assertNotNull(result);
        assertEquals(5000, result.getAmount());
        assertEquals("회원 가입 적립", result.getComment());
        verify(queryDslDefaultPointPolicyRepository, times(1)).findDefaultPointPolicyByType(PointPolicyType.REGISTER);
        verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
    }

    @Test
    void testCreateRegisterPointHistory_AlreadyExists() {
        Long memberId = 1L;

        PointHistory existingHistory = new PointHistory(HistoryTypes.EARN, 5000, LocalDateTime.now(), "회원 가입 적립", memberId);

        when(pointHistoryRepository.findPointHistoryByComment(memberId, "회원 가입 적립")).thenReturn(Optional.of(existingHistory));

        assertThrows(RuntimeException.class, () -> pointService.createRegisterPointHistory(memberId));

        verify(pointHistoryRepository, times(1)).findPointHistoryByComment(memberId, "회원 가입 적립");
        verify(pointHistoryRepository, never()).save(any(PointHistory.class));
    }
}