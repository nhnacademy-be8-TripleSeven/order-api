package com.tripleseven.orderapi.dto.cartitem;

import lombok.Getter;

import java.util.List;

@Getter
public class WrappingCartItemDTO {
    List<CartItemDTO> cartItemList;

    public void ofCreate(List<CartItemDTO> cartItemList){
        this.cartItemList = cartItemList;
    }
}

