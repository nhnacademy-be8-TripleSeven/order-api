package com.example.orderapi.entity.paytypes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PayTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    // Static factory method for creation
    public static PayTypes ofCreate(String name) {
        PayTypes payTypes = new PayTypes();
        payTypes.name = name;
        return payTypes;
    }

    // Method for updating
    public PayTypes ofUpdate(String name) {
        this.name = name;
        return this;
    }
}