package com.example.orderapi.dto.ordergroup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class OrderGroupCreateRequest {
    Long userId;

    Long wrappingId;

    ZonedDateTime orderedAt;

    String recipientName;

    String recipientPhone;

    int deliveryPrice;

    public OrderGroupCreateRequest(Long userId, Long wrappingId, String recipientName, String recipientPhone, int deliveryPrice){
        this.userId = userId;
        this.wrappingId = wrappingId;
        orderedAt = ZonedDateTime.now();
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.deliveryPrice = deliveryPrice;
    }
}
