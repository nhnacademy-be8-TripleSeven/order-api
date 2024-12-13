package com.example.orderapi.dto.deliveryinfo;

import lombok.Value;

@Value
public class DeliveryInfoCreateRequest {
    String name;

    int invoiceNumber;
}
