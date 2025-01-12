package com.tripleseven.orderapi.business.order;

import com.tripleseven.orderapi.dto.order.OrderPayDetailDTO;

public interface OrderService {
    OrderPayDetailDTO getOrderPayDetail(Long orderGroupId);
}
