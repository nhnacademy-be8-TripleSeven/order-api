package com.tripleseven.orderapi.dto.deliveryinfo;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class DeliveryInfoArrivedAtUpdateRequestDTO {
    @NotNull
    ZonedDateTime arrivedAt;
}
