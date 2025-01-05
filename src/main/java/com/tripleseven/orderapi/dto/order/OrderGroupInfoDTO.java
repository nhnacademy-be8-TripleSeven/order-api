package com.tripleseven.orderapi.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderGroupInfoDTO {
    int primeTotalPrice;
    int discountedPrice;
    int deliveryPrice;
    String wrappingName;
    int wrappingPrice;
    int totalPrice;
    int usedPoint;
    int earnedPoint;
}
