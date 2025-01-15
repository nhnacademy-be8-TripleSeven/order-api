package com.tripleseven.orderapi.dto.cartitem;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonTypeName("CartItemDTO")
public class CartItemDTO implements Serializable {
    private Long bookId;
    private String name;
    private String coverUrl;
    private int regularPrice;
    private int salePrice;
    private int quantity;
}


