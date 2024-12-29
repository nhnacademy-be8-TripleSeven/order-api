package com.tripleseven.orderapi.exception.notfound;

public class DeliveryCodeNotFoundException extends RuntimeException {
    public DeliveryCodeNotFoundException(String name) {
        super("DeliveryCode Not Found: " + name);
    }
}
