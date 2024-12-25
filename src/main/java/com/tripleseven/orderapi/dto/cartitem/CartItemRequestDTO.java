package com.tripleseven.orderapi.dto.cartitem;

import lombok.Value;

import java.util.List;

@Value
public class CartItemRequestDTO {
    List<CartItemDTO> cartItems;
}
