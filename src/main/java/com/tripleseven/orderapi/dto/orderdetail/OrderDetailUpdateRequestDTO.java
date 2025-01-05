package com.tripleseven.orderapi.dto.orderdetail;

import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import lombok.Value;

import java.util.List;

@Value
public class OrderDetailUpdateRequestDTO {
    List<Long> orderIds;
    OrderStatus orderStatus;
}
