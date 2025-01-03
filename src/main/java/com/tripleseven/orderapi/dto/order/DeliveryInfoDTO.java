package com.tripleseven.orderapi.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DeliveryInfoDTO {
    String deliveryInfoName;
    Integer invoiceNumber;
    LocalDate arrivedAt;
    Long orderId;
    LocalDate orderedAt;
    String OrdererName;
    String recipientName;
    String recipientPhone;
    String address;
}
