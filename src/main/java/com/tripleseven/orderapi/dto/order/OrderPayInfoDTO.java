package com.tripleseven.orderapi.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class OrderPayInfoDTO {
    int totalPrice;
    String paymentKey;
    String paymentName;
    LocalDate requestedAt;
}
