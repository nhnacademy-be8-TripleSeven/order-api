package com.example.orderapi.dto.wrapping;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class WrappingCreateRequest {
    @NotNull
    @NotBlank
    String name;

    @NotNull
    @Min(100)
    int price;
}
