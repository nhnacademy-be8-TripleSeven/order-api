package com.tripleseven.orderapi.dto.cartitem;


import lombok.Builder;
import lombok.Getter;

@Getter
public class CartItemResponseDTO {
    private final Long bookId;
    private final int amount;
    private final int discountedPrice;
    private final int primePrice;

    @Builder
    private CartItemResponseDTO(Long bookId, int amount, int discountedPrice, int primePrice) {
        this.bookId = bookId;
        this.amount = amount;
        this.discountedPrice = discountedPrice;
        this.primePrice = primePrice;
    }

    public static CartItemResponseDTO fromEntity(CartItemDTO cartItem) {
        return CartItemResponseDTO.builder()
                .bookId(cartItem.getBookId())
                .amount(cartItem.getAmount())
                .discountedPrice(cartItem.getDiscountPrice())
                .primePrice(cartItem.getPrimePrice())
                .build();
    }
}
