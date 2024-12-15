package com.example.orderapi.service.paytypes;

import com.example.orderapi.dto.paytypes.PayTypesResponse;
import com.example.orderapi.entity.paytypes.PayTypes;

import java.util.List;

public interface PayTypesService {
    List<PayTypesResponse> getAllPayTypes();  // 모든 결제 유형 조회
    PayTypesResponse createPayType(PayTypes payTypes);  // 결제 유형 생성
    PayTypesResponse getPayTypeById(Long id);  // 결제 유형 ID로 조회
    void removePayType(Long id);  // 결제 유형 삭제
    PayTypesResponse updatePayType(PayTypes payTypes);  // 결제 유형 업데이트
}