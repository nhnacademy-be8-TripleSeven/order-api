package com.tripleseven.orderapi.dto.order;

import com.tripleseven.orderapi.entity.orderdetail.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderInfoDTO {
    Status status;
    String bookName;
    int amount;
    int discountPrice;
    int primePrice;
}
