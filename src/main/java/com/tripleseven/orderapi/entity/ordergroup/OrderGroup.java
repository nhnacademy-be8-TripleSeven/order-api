package com.tripleseven.orderapi.entity.ordergroup;

import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.ZonedDateTime;

@Entity
@Getter
public class OrderGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 비회원은 NULL
    private Long userId;

    @NotNull
    private String orderedName;

    @NotNull
    private ZonedDateTime orderedAt;

    @NotNull
    private String recipientName;

    @NotNull
    private String recipientPhone;

    @NotNull
    private int deliveryPrice;

    @NotNull
    private String address;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "wrapping_id", nullable = false)
    private Wrapping wrapping;

    public void ofCreate(Long userId, String orderedName, String recipientName, String recipientPhone, int deliveryPrice, String address, Wrapping wrapping) {
        this.userId = userId;
        this.orderedName = orderedName;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.deliveryPrice = deliveryPrice;
        this.address = address;
        this.orderedAt = ZonedDateTime.now();
        this.wrapping = wrapping;
    }

    public void ofUpdate(String address) {
        this.address = address;
    }
}
