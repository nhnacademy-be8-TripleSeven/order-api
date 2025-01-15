package com.tripleseven.orderapi.entity.paytype;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PayType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Static factory method for creation
    public static PayType ofCreate(String name) {
        PayType payType = new PayType();
        payType.name = name;
        return payType;
    }

    // Method for updating
    public PayType ofUpdate(String name) {
        this.name = name;
        return this;
    }
}