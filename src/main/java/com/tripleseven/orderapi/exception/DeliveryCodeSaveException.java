package com.tripleseven.orderapi.exception;

public class DeliveryCodeSaveException extends RuntimeException {
    public DeliveryCodeSaveException(String message) {
        super("DeliveryCode Save Falied: " + message);
    }
}
