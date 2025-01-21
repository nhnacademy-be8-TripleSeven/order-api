package com.tripleseven.orderapi.business.rabbit;

import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import org.springframework.lang.Nullable;

import java.util.List;

public interface RabbitService {
    void sendCartMessage(@Nullable Long userId, @Nullable String guestId, List<OrderBookInfoDTO> bookInfos);

    void sendPointMessage(Long userId, Long orderId, long totalAmount);
}
