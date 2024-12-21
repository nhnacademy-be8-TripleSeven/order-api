package com.tripleseven.orderapi.exception.notfound;

public class PayTypeNotFoundException extends RuntimeException {
    public PayTypeNotFoundException(String message) {
        super(message);
    }
}
