package com.example.orderapi.dto.pay;

import lombok.Data;

@Data
public class PaymentResponse {

    private String paymentUrl;  // 결제 URL (사용자가 결제를 진행할 URL)
    private String paymentId;   // 결제 ID
    private String status;      // 결제 상태 (예: "SUCCESS", "FAIL")

    public PaymentResponse(String paymentUrl, String paymentId, String status) {
        this.paymentUrl = paymentUrl;
        this.paymentId = paymentId;
        this.status = status;
    }
}