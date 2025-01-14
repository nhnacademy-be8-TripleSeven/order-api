package com.tripleseven.orderapi.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderBookInfoDTO {
    private Long bookId;
    private String title;
    private int price; //판매가
    private int quantity;
    private int couponSalePrice; //쿠폰으로 할인된 가격
}
