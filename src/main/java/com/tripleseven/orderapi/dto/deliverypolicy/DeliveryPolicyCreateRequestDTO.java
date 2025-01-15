package com.tripleseven.orderapi.dto.deliverypolicy;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class DeliveryPolicyCreateRequestDTO {
    @NotNull
    @NotBlank
    String name;

    @NotNull
    @Min(100)
    int minPrice;

    @NotNull
    @Min(100)
    int price;
}
