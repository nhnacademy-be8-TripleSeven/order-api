package com.tripleseven.orderapi.dto.defaultpointpolicy;

import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DefaultPointPolicyDTO {
    Long id;
    PointPolicyType type;
    Long pointPolicyId;
    String name;
    int amount;
    BigDecimal rate;
}
