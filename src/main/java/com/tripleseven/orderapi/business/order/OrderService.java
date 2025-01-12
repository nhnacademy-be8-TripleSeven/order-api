package com.tripleseven.orderapi.business.order;

import com.tripleseven.orderapi.dto.order.*;

import java.util.List;

public interface OrderService {
    OrderPayDetailDTO getOrderPayDetail(Long orderGroupId);
}
