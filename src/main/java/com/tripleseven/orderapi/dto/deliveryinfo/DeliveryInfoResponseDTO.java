package com.tripleseven.orderapi.dto.deliveryinfo;

import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Slf4j
public class DeliveryInfoResponseDTO {
    private final Long id;

    private final String name;

    private final int invoiceNumber;

    private final LocalDate arrivedAt;

    private final LocalDate shippingAt;

    @Builder
    private DeliveryInfoResponseDTO(Long id, String name, int invoiceNumber, LocalDate arrivedAt, LocalDate shippingAt) {
        if (Objects.isNull(id)) {
            log.error("DeliveryInfo id cannot be null");
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
        this.name = name;
        this.invoiceNumber = invoiceNumber;
        this.arrivedAt = arrivedAt;
        this.shippingAt = shippingAt;
    }

    public static DeliveryInfoResponseDTO fromEntity(DeliveryInfo deliveryInfo) {
        return DeliveryInfoResponseDTO.builder()
                .id(deliveryInfo.getId())
                .name(deliveryInfo.getName())
                .invoiceNumber(deliveryInfo.getInvoiceNumber())
                .arrivedAt(deliveryInfo.getArrivedAt())
                .shippingAt(deliveryInfo.getShippingAt())
                .build();
    }
}
