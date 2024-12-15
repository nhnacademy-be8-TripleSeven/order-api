package com.example.orderapi.entity.deliveryinfo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Getter
public class DeliveryInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private int invoiceNumber;

    private ZonedDateTime forwardedAt;

    private LocalDate deliveryDate;

    private ZonedDateTime arrivedAt;

    public void ofCreate(String name, int invoiceNumber) {
        this.name = name;
        this.invoiceNumber = invoiceNumber;
    }

    public void ofUpdateLogistics(ZonedDateTime forwardedAt, LocalDate deliveryDate) {
        this.forwardedAt = forwardedAt;
        this.deliveryDate = deliveryDate;
    }

    public void ofUpdateArrived(ZonedDateTime arrivedAt) {
        this.arrivedAt = arrivedAt;
    }
}
