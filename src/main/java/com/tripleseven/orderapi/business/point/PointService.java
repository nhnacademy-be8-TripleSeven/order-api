package com.tripleseven.orderapi.business.point;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;

public interface PointService {
    PointHistoryResponseDTO createPointHistoryForPaymentSpend(Long memberId, int usedPoint, Long orderGroupId);

    PointHistoryResponseDTO createPointHistoryForPaymentEarn(Long memberId, int usedMoney, Long orderGroupId);
}
