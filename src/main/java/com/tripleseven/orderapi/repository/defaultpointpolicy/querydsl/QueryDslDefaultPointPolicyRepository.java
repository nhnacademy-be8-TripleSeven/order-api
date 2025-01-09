package com.tripleseven.orderapi.repository.defaultpointpolicy.querydsl;

import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;

import java.util.List;

public interface QueryDslDefaultPointPolicyRepository {
    DefaultPointPolicyDTO findDefaultPointPolicyByType(PointPolicyType pointPolicyType);

    List<DefaultPointPolicyDTO> findDefaultPointPolicies();
}
