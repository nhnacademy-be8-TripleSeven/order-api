package com.tripleseven.orderapi.entity.paytype;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PayType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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