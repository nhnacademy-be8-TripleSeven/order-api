package com.example.orderapi.dto.ordergroup;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderGroupUpdateRequest {
    private Long deliveryInfoId;
}