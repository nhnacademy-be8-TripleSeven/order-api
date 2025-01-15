package com.tripleseven.orderapi.dto.pointpolicy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class PointPolicyCreateRequestDTO {
    private String name;

    private Integer amount;

    private BigDecimal rate;
}
