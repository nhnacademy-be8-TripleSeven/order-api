package com.tripleseven.orderapi.dto.cartitem;

import lombok.Value;

import java.util.List;

@Value
public class CartUpdateRequestDTO {
    Long userId;
    List<Long> bookIds;
}
