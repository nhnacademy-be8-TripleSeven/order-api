package com.tripleseven.orderapi.dto.cartitem;

import com.tripleseven.orderapi.domain.CartItem;
import lombok.Value;

import java.util.List;

@Value
public class CartItemRequestDTO {
    List<CartItem> cartItems;
}
