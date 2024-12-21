package com.tripleseven.orderapi.dto.ordergroup;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class OrderGroupUpdateDeliveryInfoRequest {
    @NotNull
    Long deliveryInfoId;
}