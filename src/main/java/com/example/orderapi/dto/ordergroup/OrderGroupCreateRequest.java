package com.example.orderapi.dto.ordergroup;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class OrderGroupCreateRequest {
    Long userId;

    Long wrappingId;

    String orderedName;

    ZonedDateTime orderedAt;

    String recipientName;

    String recipientPhone;

    int deliveryPrice;

    public OrderGroupCreateRequest(Long userId, Long wrappingId, String orderedName, String recipientName, String recipientPhone, int deliveryPrice) {
        this.userId = userId;
        this.wrappingId = wrappingId;
        this.orderedName = orderedName;
        orderedAt = ZonedDateTime.now();
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.deliveryPrice = deliveryPrice;
    }
}
