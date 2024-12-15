package com.example.orderapi.exception.notfound.impl;

public class PayTypeNotFoundException extends RuntimeException {
    public PayTypeNotFoundException(String message) {
        super(message);
    }
}
