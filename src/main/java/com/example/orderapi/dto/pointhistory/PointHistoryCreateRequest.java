package com.example.orderapi.dto.pointhistory;

import com.example.orderapi.entity.pointhistory.HistoryTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;


//주문 외의 포인트 적립 이력 생성
@AllArgsConstructor
@Getter
public class PointHistoryCreateRequest {

    private HistoryTypes types;

    private Long pointPolicyId;
}
