package com.tripleseven.orderapi.exception;

public class PointNotEnoughException extends RuntimeException {
    public PointNotEnoughException(String message) {
        super(message);
    }
}
