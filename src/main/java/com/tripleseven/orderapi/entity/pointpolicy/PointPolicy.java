package com.tripleseven.orderapi.entity.pointpolicy;

import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyCreateRequestDTO;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyUpdateRequestDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PointPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int amount;

    private BigDecimal rate;

    // 엔티티 생성
    public static PointPolicy ofCreate(PointPolicyCreateRequestDTO request) {
        return new PointPolicy(
                null,
                request.getName(),
                request.getAmount(),
                request.getRate()
        );
    }

    // 엔티티 업데이트
    public void ofUpdate(PointPolicyUpdateRequestDTO request) {
        this.name = request.getName();
        this.amount = request.getAmount();
        this.rate = request.getRate();
    }
}