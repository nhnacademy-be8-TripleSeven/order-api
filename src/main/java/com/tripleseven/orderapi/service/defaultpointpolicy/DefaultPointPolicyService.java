package com.tripleseven.orderapi.service.defaultpointpolicy;

import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;

import java.util.List;

public interface DefaultPointPolicyService {
    DefaultPointPolicyDTO getDefaultPointPolicyDTO(PointPolicyType pointPolicyType);

    List<DefaultPointPolicyDTO> getDefaultPointPolicies();

    Long updateDefaultPoint(DefaultPointPolicyUpdateRequestDTO request);

    DefaultPointPolicyDTO getDefaultPointPolicy(PointPolicyType type);

}
