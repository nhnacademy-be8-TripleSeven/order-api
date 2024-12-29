package com.tripleseven.orderapi.exception.notfound;

public class DeliveryPolicyNotFoundException extends RuntimeException {
    public DeliveryPolicyNotFoundException(Long id) {
        super("DeliveryPolicy Not Found: " + id);
    }
}
