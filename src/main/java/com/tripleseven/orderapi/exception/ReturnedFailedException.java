package com.tripleseven.orderapi.exception;

public class ReturnedFailedException extends RuntimeException {
    public ReturnedFailedException(String message) {
        super(message);
    }
}
