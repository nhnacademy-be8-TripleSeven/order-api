package com.tripleseven.orderapi.entity.deliveryinfo;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Getter
public class DeliveryInfo {
    @Id
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private int invoiceNumber;

    private ZonedDateTime arrivedAt;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_group_id")
    private OrderGroup orderGroup;

    public void ofCreate(String name, int invoiceNumber, OrderGroup orderGroup) {
        this.name = name;
        this.invoiceNumber = invoiceNumber;
        this.orderGroup = orderGroup;
    }

    public void ofUpdateArrived(ZonedDateTime arrivedAt) {
        this.arrivedAt = arrivedAt;
    }
}
