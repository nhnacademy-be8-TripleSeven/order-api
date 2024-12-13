package com.example.orderapi.dto.deliverypolicy;

import lombok.Value;

@Value
public class DeliveryPolicyCreateRequest {
    String name;

    int price;
}
