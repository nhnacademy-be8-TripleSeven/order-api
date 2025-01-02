package com.tripleseven.orderapi.exception.notfound;

public class OrderDetailNotFoundException extends RuntimeException {
    public OrderDetailNotFoundException(Long id) {
        super("OrderDetail Not Found: " + id);
    }
}
