package com.tripleseven.orderapi.business.order;

import com.tripleseven.orderapi.dto.order.OrderPayDetailDTO;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    OrderPayDetailDTO getOrderPayDetail(Long userId, Long orderGroupId);

    OrderPayDetailDTO getOrderPayDetailAdmin(Long orderGroupId);
}
