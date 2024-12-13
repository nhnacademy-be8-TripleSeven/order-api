package com.example.orderapi.dto.deliveryinfo;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class DeliveryInfoArrivedAtUpdateRequest {
    ZonedDateTime arrivedAt;
}
