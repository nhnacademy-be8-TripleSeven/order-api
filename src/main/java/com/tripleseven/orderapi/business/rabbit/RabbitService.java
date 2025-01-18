package com.tripleseven.orderapi.business.rabbit;

import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;

import java.util.List;

public interface RabbitService {
    void sendCartMessage(String userId, List<OrderBookInfoDTO> bookInfos);

    void sendPointMessage(Long userId, Long orderId, long totalAmount);
}
