package com.tripleseven.orderapi.business.pointservice;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;

public interface PointService {
    PointHistoryResponseDTO createPointHistoryForPaymentSpend(Long memberId, int usedPoint);

    PointHistoryResponseDTO createPointHistoryForPaymentEarn(Long memberId, int usedMoney);

    PointHistoryResponseDTO createPointHistoryByAmount(Long memberId, Long policyId);

    PointHistoryResponseDTO createPointHistoryByRate(Long memberId, int usedMoney, Long policyId);
}
