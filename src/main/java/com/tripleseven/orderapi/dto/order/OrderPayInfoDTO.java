package com.tripleseven.orderapi.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public class OrderPayInfoDTO {
    long totalPrice;
    String paymentKey;
    String paymentName;
    LocalDate requestedAt;

}
