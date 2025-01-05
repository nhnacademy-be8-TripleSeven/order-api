package com.tripleseven.orderapi.exception;

public class CancelFailedException extends RuntimeException {
    public CancelFailedException(String message) {
        super(message);
    }
}
