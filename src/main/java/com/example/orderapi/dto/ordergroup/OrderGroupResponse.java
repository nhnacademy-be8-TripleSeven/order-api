package com.example.orderapi.dto.ordergroup;

import com.example.orderapi.entity.ordergroup.OrderGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Slf4j
public class OrderGroupResponse {
    private final Long id;

    private final Long userId;

    private final Long wrappingId;

    private final Long deliveryInfoId;

    private final String orderedName;

    private final ZonedDateTime orderedAt;

    private final String recipientName;

    private final String recipientPhone;

    private final int deliveryPrice;

    @Builder
    private OrderGroupResponse(Long id, Long userId, Long wrappingId, Long deliveryInfoId, String orderedName, ZonedDateTime orderedAt, String recipientName, String recipientPhone, int deliveryPrice) {
        if (Objects.isNull(id)) {
            log.error("OrderGroup id cannot be null");
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
        this.userId = userId;
        this.wrappingId = wrappingId;
        this.deliveryInfoId = deliveryInfoId;
        this.orderedName = orderedName;
        this.orderedAt = orderedAt;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.deliveryPrice = deliveryPrice;
    }

    public static OrderGroupResponse fromEntity(OrderGroup orderGroup) {
        return OrderGroupResponse.builder()
                .id(orderGroup.getId())
                .userId(orderGroup.getUserId())
                .wrappingId(orderGroup.getWrappingId())
                .deliveryInfoId(orderGroup.getDeliveryInfoId())
                .orderedName(orderGroup.getOrderedName())
                .orderedAt(orderGroup.getOrderedAt())
                .recipientName(orderGroup.getRecipientName())
                .recipientPhone(orderGroup.getRecipientPhone())
                .deliveryPrice(orderGroup.getDeliveryPrice())
                .build();
    }
}
