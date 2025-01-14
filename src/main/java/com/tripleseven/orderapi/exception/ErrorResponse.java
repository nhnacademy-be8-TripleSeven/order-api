package com.tripleseven.orderapi.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private int statusCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
    private String message;
    private String requestPath;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, HttpServletRequest request) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .localDateTime(LocalDateTime.now())
                        .statusCode(errorCode.getHttpStatus().value())
                        .message(errorCode.getMessage())
                        .requestPath(request.getRequestURI())
                        .build()
                );
    }
}
