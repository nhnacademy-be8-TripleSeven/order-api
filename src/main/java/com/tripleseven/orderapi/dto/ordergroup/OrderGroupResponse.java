package com.tripleseven.orderapi.dto.ordergroup;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.environmentutils.EnvironmentUtil;
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

    private final String address;

    @Builder
    private OrderGroupResponse(Long id, Long userId, Long wrappingId, Long deliveryInfoId, String orderedName, ZonedDateTime orderedAt, String recipientName, String recipientPhone, int deliveryPrice, String address) {
        if (!EnvironmentUtil.isTestEnvironment() && Objects.isNull(id)) {
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
        this.address = address;
    }

    public static OrderGroupResponse fromEntity(OrderGroup orderGroup) {
        if (Objects.isNull(orderGroup.getDeliveryInfo())){
            return OrderGroupResponse.builder()
                    .id(orderGroup.getId())
                    .userId(orderGroup.getUserId())
                    .orderedName(orderGroup.getOrderedName())
                    .orderedAt(orderGroup.getOrderedAt())
                    .recipientName(orderGroup.getRecipientName())
                    .recipientPhone(orderGroup.getRecipientPhone())
                    .address(orderGroup.getAddress())
                    .deliveryPrice(orderGroup.getDeliveryPrice())
                    .wrappingId(orderGroup.getWrapping().getId())
                    .build();
        }
        return OrderGroupResponse.builder()
                .id(orderGroup.getId())
                .userId(orderGroup.getUserId())
                .orderedName(orderGroup.getOrderedName())
                .orderedAt(orderGroup.getOrderedAt())
                .recipientName(orderGroup.getRecipientName())
                .recipientPhone(orderGroup.getRecipientPhone())
                .deliveryPrice(orderGroup.getDeliveryPrice())
                .address(orderGroup.getAddress())
                .wrappingId(orderGroup.getWrapping().getId())
                .deliveryInfoId(orderGroup.getDeliveryInfo().getId())
                .build();
    }
}
