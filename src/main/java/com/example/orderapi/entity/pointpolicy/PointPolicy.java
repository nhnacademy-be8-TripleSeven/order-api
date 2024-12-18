package com.example.orderapi.entity.pointpolicy;

import com.example.orderapi.dto.pointpolicy.PointPolicyCreateRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@Setter @Getter
@NoArgsConstructor
public class PointPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int amount;

    private BigDecimal rate;

    public PointPolicy ofCreate(PointPolicyCreateRequest request){
        this.name = request.getName();
        this.amount = request.getAmount();
        this.rate = request.getRate();

        return this;
    }

}
