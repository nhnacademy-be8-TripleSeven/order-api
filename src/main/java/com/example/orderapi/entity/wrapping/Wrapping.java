package com.example.orderapi.entity.wrapping;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Getter
public class Wrapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private int price;

    public void ofCreate(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public void ofUpdate(String name, int price) {
        this.name = name;
        this.price = price;
    }
}
