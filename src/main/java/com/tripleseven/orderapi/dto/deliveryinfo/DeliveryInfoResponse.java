package com.tripleseven.orderapi.dto.deliveryinfo;

import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.environmentutils.EnvironmentUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Slf4j
public class DeliveryInfoResponse {
    private final Long id;

    private final String name;

    private final int invoiceNumber;

    private final ZonedDateTime forwardedAt;

    private final LocalDate deliveryDate;

    private final ZonedDateTime arrivedAt;

    @Builder
    private DeliveryInfoResponse(Long id, String name, int invoiceNumber, ZonedDateTime forwardedAt, LocalDate deliveryDate, ZonedDateTime arrivedAt) {
        if (!EnvironmentUtil.isTestEnvironment() && Objects.isNull(id)) {
            log.error("DeliveryInfo id cannot be null");
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
        this.name = name;
        this.invoiceNumber = invoiceNumber;
        this.forwardedAt = forwardedAt;
        this.deliveryDate = deliveryDate;
        this.arrivedAt = arrivedAt;
    }

    public static DeliveryInfoResponse fromEntity(DeliveryInfo deliveryInfo) {
        return DeliveryInfoResponse.builder()
                .id(deliveryInfo.getId())
                .name(deliveryInfo.getName())
                .invoiceNumber(deliveryInfo.getInvoiceNumber())
                .forwardedAt(deliveryInfo.getForwardedAt())
                .deliveryDate(deliveryInfo.getDeliveryDate())
                .arrivedAt(deliveryInfo.getArrivedAt())
                .build();
    }
}
