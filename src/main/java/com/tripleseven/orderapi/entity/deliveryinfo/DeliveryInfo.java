package com.tripleseven.orderapi.entity.deliveryinfo;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class DeliveryInfo {
    @Id
    private Long id;

    private String name;

    private int invoiceNumber;

    private LocalDate arrivedAt;

    private LocalDate shippingAt;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_group_id")
    private OrderGroup orderGroup;

    public void ofCreate(OrderGroup orderGroup) {
        this.orderGroup = orderGroup;
    }

    public void ofUpdate(String name, int invoiceNumber, LocalDate arrivedAt) {
        this.name = name;
        this.invoiceNumber = invoiceNumber;
        this.arrivedAt = arrivedAt;
    }

    public void ofShippingUpdate(LocalDate shippingAt){
        this.shippingAt = shippingAt;
    }
}
