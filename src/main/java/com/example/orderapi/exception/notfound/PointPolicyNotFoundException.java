package com.example.orderapi.exception.notfound;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PointPolicyNotFoundException extends RuntimeException {
    public PointPolicyNotFoundException(String message) {
        super(message);
    }
}
