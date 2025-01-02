package com.tripleseven.orderapi.exception.handler;

import com.tripleseven.orderapi.exception.KeyManagerException;
import com.tripleseven.orderapi.exception.PointNotEnoughException;
import com.tripleseven.orderapi.exception.notfound.PayTypeNotFoundException;
import com.tripleseven.orderapi.exception.notfound.PointHistoryNotFoundException;
import com.tripleseven.orderapi.exception.notfound.PointPolicyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PayTypeNotFoundException.class)
    public ResponseEntity<String> handlePayTypeNotFoundException(PayTypeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PointHistoryNotFoundException.class)
    public ResponseEntity<String> handlePointHistoryNotFoundException(PointHistoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(PointPolicyNotFoundException.class)
    public ResponseEntity<String> handlePointPolicyNotFoundException(PointPolicyNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(KeyManagerException.class)
    public ResponseEntity<String> handleKeyManagerException(KeyManagerException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PointNotEnoughException.class)
    public ResponseEntity<String> handlePointNotEnoughException(PointNotEnoughException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
