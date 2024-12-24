package com.tripleseven.orderapi.dto.cartitem;

import com.tripleseven.orderapi.domain.CartItem;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CartItemResponseDTO {
    private final Long bookId;
    private final Long amount;
    private final int discountedPrice;
    private final int totalPrice;

    @Builder
    private CartItemResponseDTO(Long bookId, Long amount, int discountedPrice, int totalPrice) {
        this.bookId = bookId;
        this.amount = amount;
        this.discountedPrice = discountedPrice;
        this.totalPrice = totalPrice;
    }

    public static CartItemResponseDTO fromEntity(CartItem cartItem) {
        return CartItemResponseDTO.builder()
                .bookId(cartItem.getBookId())
                .amount(cartItem.getAmount())
                .discountedPrice(cartItem.getDiscountPrice())
                .totalPrice(cartItem.getPrimePrice())
                .build();
    }
}
