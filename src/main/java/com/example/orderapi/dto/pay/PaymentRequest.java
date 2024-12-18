package com.example.orderapi.dto.pay;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Order ID is required")
    private String orderId;  // 주문 ID

    @Min(value = 1, message = "Amount must be greater than 0")
    private int amount;  // 결제 금액

    @NotNull(message = "Return URL is required")
    private String returnUrl;  // 결제 완료 후 돌아갈 URL

    public PaymentRequest(String orderId, int amount, String returnUrl) {
        this.orderId = orderId;
        this.amount = amount;
        this.returnUrl = returnUrl;
    }
}