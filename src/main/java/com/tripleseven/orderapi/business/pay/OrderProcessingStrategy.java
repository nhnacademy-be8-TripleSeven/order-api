package com.tripleseven.orderapi.business.pay;

public interface OrderProcessingStrategy {
    void processSingleOrder();

    void processMultipleOrder();
}
