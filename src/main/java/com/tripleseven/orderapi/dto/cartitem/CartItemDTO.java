package com.tripleseven.orderapi.dto.cartitem;

import lombok.Getter;

@Getter
public class CartItemDTO {
    Long bookId;
    int amount;
    int discountPrice;
    int PrimePrice;
}
