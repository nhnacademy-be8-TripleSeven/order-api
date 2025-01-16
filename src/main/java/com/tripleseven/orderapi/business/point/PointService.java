package com.tripleseven.orderapi.business.point;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;

public interface PointService {
    PointHistoryResponseDTO createPointHistoryForPaymentSpend(Long memberId, long usedPoint, Long orderGroupId);

    PointHistoryResponseDTO createPointHistoryForPaymentEarn(Long memberId, long usedMoney, Long orderGroupId);

    PointHistoryResponseDTO createRegisterPointHistory(Long memberId);
}
