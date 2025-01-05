package com.tripleseven.orderapi.dto.deliveryinfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class DeliveryInfoCreateRequestDTO {
    @NotNull
    @NotBlank
    Long orderGroupId;
}
