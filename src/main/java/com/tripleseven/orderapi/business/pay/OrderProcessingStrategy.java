package com.tripleseven.orderapi.business.pay;

import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;

public interface OrderProcessingStrategy {
    void processSingleOrder(OrderGroupCreateRequestDTO orderGroupCreateRequestDTO);

    void processMultipleOrder(OrderGroupCreateRequestDTO orderGroupCreateRequestDTO);
}
