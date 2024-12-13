package com.example.orderapi.dto.wrapping;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class WrappingCreateRequest {
    @NotNull
    String name;
    @NotNull
    int price;
}
