package com.tripleseven.orderapi.entity.pointpolicy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@Getter @Setter
@NoArgsConstructor
@Data
public class PointPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int amount;

    private BigDecimal rate;

}
