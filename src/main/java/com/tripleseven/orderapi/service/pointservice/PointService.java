package com.tripleseven.orderapi.service.pointservice;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;

public interface PointService {
    PointHistoryResponseDTO createPointHistoryForPaymentSpend(Long memberId, int usedPoint);
    PointHistoryResponseDTO createPointHistoryForPaymentEarn(Long memberId, int usedMoney, Long pointPolicyId);
}
