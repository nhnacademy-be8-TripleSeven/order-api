package com.tripleseven.orderapi.dto.pointpolicy;

import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class PointPolicyResponse {
    private final Long id;
    private final String name;
    private final int amount;
    private final BigDecimal rate;

    @Builder
    public PointPolicyResponse(Long id, String name, int amount, BigDecimal rate) {
        // Validate input parameters
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (Objects.isNull(name)|| name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (Objects.isNull(rate)|| rate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Rate cannot be null or negative");
        }

        this.id = id;
        this.name = name;
        this.amount = amount;
        this.rate = rate;
    }

    public static PointPolicyResponse fromEntity(PointPolicy pointPolicy) {

        return new PointPolicyResponse(
                pointPolicy.getId(),
                pointPolicy.getName(),
                pointPolicy.getAmount(),
                pointPolicy.getRate());
    }
}