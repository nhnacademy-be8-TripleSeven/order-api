package com.tripleseven.orderapi.dto.defaultpointpolicy;

import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import lombok.Value;

@Value
public class DefaultPointPolicyUpdateRequestDTO {
    Long pointPolicyId;
    PointPolicyType pointPolicyType;
}
