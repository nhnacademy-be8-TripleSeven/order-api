package com.tripleseven.orderapi.business.point;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;

public interface PointService {
    PointHistoryResponseDTO createPointHistoryForPaymentSpend(Long memberId, int usedPoint, Long orderGroupId);

    PointHistoryResponseDTO createPointHistoryForPaymentEarn(Long memberId, int usedMoney, Long orderGroupId);

    PointHistoryResponseDTO createPointHistoryByAmount(Long memberId, Long policyId, Long orderGroupId);

    PointHistoryResponseDTO createPointHistoryByRate(Long memberId, int usedMoney, Long policyId, Long orderGroupId);
}
