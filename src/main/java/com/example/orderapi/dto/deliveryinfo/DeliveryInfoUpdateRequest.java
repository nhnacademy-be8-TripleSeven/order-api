package com.example.orderapi.dto.deliveryinfo;

import lombok.Value;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Value
public class DeliveryInfoUpdateRequest {
    ZonedDateTime forwardedAt;

    LocalDate deliveryDate;

    ZonedDateTime arrivedAt;
}
