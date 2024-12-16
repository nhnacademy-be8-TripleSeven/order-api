package com.example.orderapi.dto.deliveryinfo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

@Value
public class DeliveryInfoCreateRequest {
    @NotNull
    @NotBlank
    String name;

    @Min(0)
    @Length(min = 8)
    int invoiceNumber;
}
