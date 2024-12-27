package com.tripleseven.orderapi.dto.cartitem;

import lombok.Getter;

@Getter
public class CartItemDTO {
    Long bookId;
    int amount;
    int discountPrice;
    int primePrice;

    public void ofCreateTest(){
        this.bookId = 1L;
        this.amount = 2;
        this.discountPrice = 9000;
        this.primePrice = 10000;
    }
}
