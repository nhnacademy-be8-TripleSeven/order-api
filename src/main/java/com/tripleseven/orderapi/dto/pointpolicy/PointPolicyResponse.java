package com.tripleseven.orderapi.dto.pointpolicy;

import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PointPolicyResponse {
    private Long id;
    private String name;
    private int amount;
    private BigDecimal rate;

    public static PointPolicyResponse fromEntity(PointPolicy pointPolicy) {
        PointPolicyResponse pointPolicyResponse = new PointPolicyResponse();

        pointPolicyResponse.setId(pointPolicy.getId());
        pointPolicyResponse.setName(pointPolicy.getName());
        pointPolicyResponse.setAmount(pointPolicy.getAmount());
        pointPolicyResponse.setRate(pointPolicy.getRate());

        return pointPolicyResponse;
    }
}