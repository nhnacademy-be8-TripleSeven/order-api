package com.example.orderapi.dto.cartitem;

import com.example.orderapi.domain.CartItem;
import lombok.Value;

import java.util.List;

@Value
public class CartItemRequest {
    List<CartItem> cartItems;
}
