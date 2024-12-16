package com.example.orderapi.dto.deliveryinfo;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Value
public class DeliveryInfoLogisticsUpdateRequest {
    @NotNull
    ZonedDateTime forwardedAt;

    @NotNull
    LocalDate deliveryDate;
}
