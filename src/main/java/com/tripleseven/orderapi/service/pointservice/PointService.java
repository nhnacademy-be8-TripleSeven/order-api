package com.tripleseven.orderapi.service.pointservice;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponse;

public interface PointService {
    PointHistoryResponse createPointHistoryForPaymentSpend(Long memberId, int usedPoint);
    PointHistoryResponse createPointHistoryForPaymentEarn(Long memberId, int usedMoney, Long pointPolicyId);
}
