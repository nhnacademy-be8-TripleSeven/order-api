package com.example.orderapi.entity.pointpolicy;

import com.example.orderapi.dto.pointpolicy.PointPolicyCreateRequest;
import com.example.orderapi.dto.pointpolicy.PointPolicyUpdateRequest;
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
    public static PointPolicy ofCreate(PointPolicyCreateRequest request) {
        return new PointPolicy(
                null,
                request.getName(),
                request.getAmount(),
                request.getRate()
        );
    }

    // 엔티티 업데이트
    public void ofUpdate(PointPolicyUpdateRequest request) {
        this.name = request.getName();
        this.amount = request.getAmount();
        this.rate = request.getRate();
    }
}