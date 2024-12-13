package com.example.orderapi.dto.deliverypolicy;

import lombok.Value;

@Value
public class DeliveryPolicyUpdateRequest {
    String name;

    int price;
}
