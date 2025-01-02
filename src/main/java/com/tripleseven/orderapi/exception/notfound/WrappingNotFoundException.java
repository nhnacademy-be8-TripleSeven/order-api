package com.tripleseven.orderapi.exception.notfound;

public class WrappingNotFoundException extends RuntimeException {
    public WrappingNotFoundException(Long id) {
        super("Wrapping Not Found: " + id);
    }
}
