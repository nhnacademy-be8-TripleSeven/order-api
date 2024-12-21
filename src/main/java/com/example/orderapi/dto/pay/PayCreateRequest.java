package com.example.orderapi.dto.pay;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PayCreateRequest {

    private String orderId;
    private int amount;
    private String orderName;
    private String customerName;
    private String customerEmail;

}
