package com.example.orderapi.dto.orderdetail;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class OrderDetailCreateRequest {
    @NotNull
    Long bookId;

    @NotNull
    @Min(1)
    int amount;

    @NotNull
    @Min(100)
    int price;

    @NotNull
    Long wrappingId;

    @NotNull
    Long orderGroupId;
}
