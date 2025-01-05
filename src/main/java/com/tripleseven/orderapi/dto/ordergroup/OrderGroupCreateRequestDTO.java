package com.tripleseven.orderapi.dto.ordergroup;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

@Value
public class OrderGroupCreateRequestDTO {
    @NotNull
    Long wrappingId;

    @NotNull
    @NotBlank
    String orderedName;

    @NotNull
    @NotBlank
    String recipientName;

    @NotNull
    @NotBlank
    @Length(max = 15)
    String recipientPhone;

    @Length(max = 15)
    String recipientHomePhone;

    @NotNull
    @Min(100)
    int deliveryPrice;

    @NotNull
    String address;
}
