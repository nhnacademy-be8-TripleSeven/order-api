package com.example.orderapi.dto.ordergroup;

import lombok.Getter;
import lombok.Value;

@Value
public class OrderGroupUpdateRequest {
    Long wrappingId;

    String recipientName;

    String recipientPhone;

    public OrderGroupUpdateRequest(Long wrappingId, String recipientName, String recipientPhone) {
        this.wrappingId = wrappingId;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
    }
}
