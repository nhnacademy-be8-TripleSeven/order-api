package com.tripleseven.orderapi.dto.pointpolicy;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class PointPolicyCreateRequestDTO {
    private String name;

    private int amount;

    private BigDecimal rate;
}
