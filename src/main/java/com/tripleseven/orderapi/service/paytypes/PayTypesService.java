package com.tripleseven.orderapi.service.paytypes;

import com.tripleseven.orderapi.dto.paytypes.PayTypeCreateRequestDTO;
import com.tripleseven.orderapi.dto.paytypes.PayTypesResponseDTO;

import java.util.List;

public interface PayTypesService {
    List<PayTypesResponseDTO> getAllPayTypes();  // 모든 결제 유형 조회
    PayTypesResponseDTO createPayType(PayTypeCreateRequestDTO request);  // 결제 유형 생성
    PayTypesResponseDTO getPayTypeById(Long id);  // 결제 유형 ID로 조회
    void removePayType(Long id);  // 결제 유형 삭제
    PayTypesResponseDTO updatePayType(Long id, PayTypeCreateRequestDTO request);  // 결제 유형 업데이트
}