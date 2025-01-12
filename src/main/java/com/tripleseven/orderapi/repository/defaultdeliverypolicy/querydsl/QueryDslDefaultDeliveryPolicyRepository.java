package com.tripleseven.orderapi.repository.defaultdeliverypolicy.querydsl;

import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;

import java.util.List;

public interface QueryDslDefaultDeliveryPolicyRepository {
    // 정책 타입 별 검색 (UNIQUE)
    List<DefaultDeliveryPolicyDTO> findDefaultDeliveryPolicy();
}
