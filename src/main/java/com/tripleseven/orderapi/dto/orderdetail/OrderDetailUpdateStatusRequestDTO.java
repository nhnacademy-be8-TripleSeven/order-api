package com.tripleseven.orderapi.dto.orderdetail;

import com.tripleseven.orderapi.entity.orderdetail.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class OrderDetailUpdateStatusRequestDTO {
    @NotNull
    @NotBlank
    Status status;
}
