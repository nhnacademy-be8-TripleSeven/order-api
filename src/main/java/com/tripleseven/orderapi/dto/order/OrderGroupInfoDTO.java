package com.tripleseven.orderapi.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderGroupInfoDTO {
    long primeTotalPrice;
    long discountedPrice;
    long deliveryPrice;
    String wrappingName;
    long wrappingPrice;
    long totalPrice;
    long usedPoint;
    long earnedPoint;
}
