package com.tripleseven.orderapi.service.paytypes;

import com.tripleseven.orderapi.dto.paytypes.PayTypeCreateRequest;
import com.tripleseven.orderapi.dto.paytypes.PayTypesResponse;

import java.util.List;

public interface PayTypesService {
    List<PayTypesResponse> getAllPayTypes();  // 모든 결제 유형 조회
    PayTypesResponse createPayType(PayTypeCreateRequest request);  // 결제 유형 생성
    PayTypesResponse getPayTypeById(Long id);  // 결제 유형 ID로 조회
    void removePayType(Long id);  // 결제 유형 삭제
    PayTypesResponse updatePayType(Long id, PayTypeCreateRequest request);  // 결제 유형 업데이트
}