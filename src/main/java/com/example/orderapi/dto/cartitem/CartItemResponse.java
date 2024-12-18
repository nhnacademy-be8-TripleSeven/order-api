package com.example.orderapi.dto.cartitem;

import com.example.orderapi.domain.CartItem;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CartItemResponse {
    private final Long bookId;
    private final Long amount;
    private final int discountedPrice;
    private final int totalPrice;

    @Builder
    private CartItemResponse(Long bookId, Long amount, int discountedPrice, int totalPrice) {
        this.bookId = bookId;
        this.amount = amount;
        this.discountedPrice = discountedPrice;
        this.totalPrice = totalPrice;
    }

    public static CartItemResponse fromEntity(CartItem cartItem) {
        return CartItemResponse.builder()
                .bookId(cartItem.getBookId())
                .amount(cartItem.getAmount())
                .discountedPrice(cartItem.getDiscountPrice())
                .totalPrice(cartItem.getPrimePrice())
                .build();
    }
}
