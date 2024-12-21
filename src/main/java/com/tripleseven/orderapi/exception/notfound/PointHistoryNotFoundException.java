package com.tripleseven.orderapi.exception.notfound;

public class PointHistoryNotFoundException extends RuntimeException {
    public PointHistoryNotFoundException(String message) {
        super(message);
    }
}
