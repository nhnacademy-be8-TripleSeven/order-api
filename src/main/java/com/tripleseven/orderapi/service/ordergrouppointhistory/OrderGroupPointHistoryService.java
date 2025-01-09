package com.tripleseven.orderapi.service.ordergrouppointhistory;

import com.tripleseven.orderapi.dto.ordergrouppointhistory.OrderGroupPointHistoryRequestDTO;
import com.tripleseven.orderapi.dto.ordergrouppointhistory.OrderGroupPointHistoryResponseDTO;

public interface OrderGroupPointHistoryService {
    int getUsedPoint(Long orderGroupId);

    int getEarnedPoint(Long orderGroupId);

    OrderGroupPointHistoryResponseDTO createOrderGroupPointHistory(OrderGroupPointHistoryRequestDTO request);
}
