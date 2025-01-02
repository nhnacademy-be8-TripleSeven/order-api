package com.tripleseven.orderapi.exception.notfound;

public class DeliveryInfoNotFoundException extends RuntimeException {
    public DeliveryInfoNotFoundException(Long id) {
        super("DeliveryInfo Not Found: " + id);
    }
}
