package com.tripleseven.orderapi.exception.notfound;

public class OrderGroupNotFoundException extends RuntimeException {
    public OrderGroupNotFoundException(Long id) {
        super("Not Found OrderGroup: " + id);
    }
}
