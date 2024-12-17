package com.example.orderapi.service.pointservice;

import com.example.orderapi.dto.pointhistory.PointHistoryResponse;

public interface PointService {
    PointHistoryResponse createPointHistoryForPaymentSpend(Long memberId, int usedPoint);
    PointHistoryResponse createPointHistoryForPaymentEarn(Long memberId, int usedMoney, Long pointPolicyId);
}
