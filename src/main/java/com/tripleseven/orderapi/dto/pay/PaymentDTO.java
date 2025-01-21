package com.tripleseven.orderapi.dto.pay;

import com.tripleseven.orderapi.entity.pay.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import org.json.simple.JSONObject;

import java.time.LocalDate;
import java.time.OffsetDateTime;


@Getter
public class PaymentDTO {
    private final Long orderId;
    private final LocalDate requestedAt;
    private final long balanceAmount;
    private final PaymentStatus status; // enum으로 변경
    private final String paymentKey;

    @Builder
    public PaymentDTO(Long orderId, LocalDate requestedAt, long balanceAmount, PaymentStatus status, String paymentKey) {
        this.orderId = orderId;
        this.requestedAt = requestedAt;
        this.balanceAmount = balanceAmount;
        this.status = status;
        this.paymentKey = paymentKey;
    }

    public static PaymentDTO fromJson(JSONObject response) {
        return PaymentDTO.builder()
                .orderId(Long.valueOf(response.get("orderId").toString()))
                .requestedAt(OffsetDateTime.parse(response.get("requestedAt").toString()).toLocalDate())
                .balanceAmount(Long.parseLong(response.get("balanceAmount").toString()))
                .status(PaymentStatus.fromString(response.get("status").toString())) // ✅ Enum 변환 적용
                .paymentKey(response.get("paymentKey").toString())
                .build();
    }
}
