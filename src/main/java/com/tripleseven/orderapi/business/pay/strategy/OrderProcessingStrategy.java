package com.tripleseven.orderapi.business.pay.strategy;

public interface OrderProcessingStrategy {
    void processSingleOrder();

    void processMultipleOrder();
}
