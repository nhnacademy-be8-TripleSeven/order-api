package com.tripleseven.orderapi.exception.notfound;

public class DefaultDeliveryPolicyNotFoundException extends RuntimeException {
    public DefaultDeliveryPolicyNotFoundException(String message) {
        super(message);
    }
}
