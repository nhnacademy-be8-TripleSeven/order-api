package com.tripleseven.orderapi.entity.pointpolicy;

import com.tripleseven.orderapi.entity.defaultpointpolicy.DefaultPointPolicy;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class PointPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int amount;

    private BigDecimal rate;

    @OneToMany(mappedBy = "pointPolicy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<DefaultPointPolicy> pointPolicies = new ArrayList<>();

    // 엔티티 생성
    public void ofCreate(String name, int amount, BigDecimal rate) {
        this.name = name;
        this.amount = amount;
        this.rate = rate;
    }

    // 엔티티 업데이트
    public void ofUpdate(String name, int amount, BigDecimal rate) {
        this.name = name;
        this.amount = amount;
        this.rate = rate;
    }

    public void addDefaultPointPolicy(DefaultPointPolicy defaultPointPolicy) {
        this.pointPolicies.add(defaultPointPolicy);
    }
}