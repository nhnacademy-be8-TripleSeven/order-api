package com.example.orderapi.business.pay.strategy;

import com.example.orderapi.dto.orderdetail.OrderDetailCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupCreateRequest;

public interface OrderProcessingStrategy {
    void processSingleOrder(OrderGroupCreateRequest orderGroupCreateRequest,
                      OrderDetailCreateRequest orderDetailCreateRequest);

    void processMultipleOrder();
}
