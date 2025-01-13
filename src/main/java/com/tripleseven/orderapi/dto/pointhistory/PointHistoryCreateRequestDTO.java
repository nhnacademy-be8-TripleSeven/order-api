package com.tripleseven.orderapi.dto.pointhistory;

import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import lombok.Value;

// 주문 외의 포인트 적립 이력 생성
@Value
public class PointHistoryCreateRequestDTO {
    HistoryTypes types;

    Long pointPolicyId;
}
