package com.example.orderapi.dto.orderdetail;

import com.example.orderapi.entity.orderdetail.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class OrderDetailUpdateStatusRequest {
    @NotNull
    @NotBlank
    Status status;
}
