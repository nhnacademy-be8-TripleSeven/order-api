package com.example.orderapi.dto.pointpolicy;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PointPolicyCreateRequest {
    private String name;

    private int amount;

    private BigDecimal rate;
}
