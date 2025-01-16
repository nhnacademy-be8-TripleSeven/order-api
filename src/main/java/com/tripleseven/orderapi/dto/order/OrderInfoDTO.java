package com.tripleseven.orderapi.dto.order;

import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderInfoDTO {
    Long orderDetailId;
    OrderStatus orderStatus;
    String bookName;
    int amount;
    long discountPrice;
    long primePrice;
}
