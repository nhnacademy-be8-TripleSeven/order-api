package com.tripleseven.orderapi.dto.order;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class OrderBookInfoDTO implements Serializable {
    private Long bookId;
    private String title;
    private int price; //판매가
    private int quantity;
    private Long couponId;
    private int couponSalePrice; //쿠폰으로 할인된 가격
}
