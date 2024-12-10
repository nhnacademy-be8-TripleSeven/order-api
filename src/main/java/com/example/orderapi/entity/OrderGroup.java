package com.example.orderapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Data
public class OrderGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long wrappingId;

    private Long deliveryInfoId;

    private ZonedDateTime orderedAt;

    private String recipientName;

    private String recipientPhone;

    private int deliveryPrice;

}
