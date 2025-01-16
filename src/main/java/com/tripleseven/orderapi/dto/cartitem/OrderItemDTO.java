package com.tripleseven.orderapi.dto.cartitem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemDTO {
    Long bookId;
    long amount;
    long discountPrice;
    long primePrice;
}
