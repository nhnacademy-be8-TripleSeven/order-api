package com.example.orderapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long wrappingId;

    private Long deliveryInfoId;

    @NotNull
    private String orderedName;


    private ZonedDateTime orderedAt;

    @NotNull
    private String recipientName;

    @NotNull
    private String recipientPhone;

    private int deliveryPrice;

}
