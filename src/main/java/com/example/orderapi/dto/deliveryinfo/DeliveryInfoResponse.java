package com.example.orderapi.dto.deliveryinfo;

import com.example.orderapi.entity.deliveryinfo.DeliveryInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class DeliveryInfoResponse {
    private Long id;

    private String name;

    private int invoiceNumber;

    private ZonedDateTime forwardedAt;

    private LocalDate deliveryDate;

    private ZonedDateTime arrivedAt;

    public static DeliveryInfoResponse fromEntity(DeliveryInfo deliveryInfo) {
        DeliveryInfoResponse dto = new DeliveryInfoResponse();

        dto.setId(deliveryInfo.getId());
        dto.setName(deliveryInfo.getName());
        dto.setInvoiceNumber(deliveryInfo.getInvoiceNumber());
        dto.setForwardedAt(deliveryInfo.getForwardedAt());
        dto.setDeliveryDate(deliveryInfo.getDeliveryDate());
        dto.setArrivedAt(deliveryInfo.getArrivedAt());

        return dto;
    }
}
