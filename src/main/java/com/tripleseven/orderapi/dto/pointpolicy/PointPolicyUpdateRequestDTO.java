package com.tripleseven.orderapi.dto.pointpolicy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class PointPolicyUpdateRequestDTO {
    private String name;

    private int amount;

    private BigDecimal rate;
}
