package com.tripleseven.orderapi.dto.orderdetail;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class OrderDetailCreateRequestDTO {
    @NotNull
    Long bookId;

    @NotNull
    @Min(1)
    int amount;

    @NotNull
    @Min(100)
    long primePrice;

    @NotNull
    @Min(100)
    long discountPrice;

    @NotNull
    Long orderGroupId;
}
