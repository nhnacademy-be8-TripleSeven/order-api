package com.tripleseven.orderapi.dto.deliveryinfo;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;

@Value
public class DeliveryInfoArrivedAtUpdateRequestDTO {
    @NotNull
    LocalDate arrivedAt;
}
