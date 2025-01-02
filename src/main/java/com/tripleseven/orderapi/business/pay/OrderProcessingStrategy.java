package com.tripleseven.orderapi.business.pay;

import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;

public interface OrderProcessingStrategy {
    void processNonMemberOrder(OrderGroupCreateRequestDTO orderGroupCreateRequestDTO);

    void processMemberOrder(Long memberId, OrderGroupCreateRequestDTO orderGroupCreateRequestDTO);
}
