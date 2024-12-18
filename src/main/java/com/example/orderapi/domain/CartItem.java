package com.example.orderapi.domain;

import lombok.Getter;

@Getter
public class CartItem {
    Long bookId;
    Long amount;
    int discountPrice;
    int PrimePrice;
}
