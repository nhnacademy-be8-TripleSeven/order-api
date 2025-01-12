package com.tripleseven.orderapi.dto.defaultpointpolicy;

import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
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

    public DefaultPointPolicyDTO(Long id, PointPolicyType type, PointPolicy pointPolicy){
        this.id = id;
        this.type = type;
        this.pointPolicyId = pointPolicy.getId();
        this.name = pointPolicy.getName();
        this.amount = pointPolicy.getAmount();
        this.rate = pointPolicy.getRate();
    }
}
