package com.example.orderapi.dto.ordergroup;

import com.example.orderapi.entity.OrderGroup;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class OrderGroupResponse {
    private Long id;

    private Long userId;

    private Long wrappingId;

    private Long deliveryInfoId;

    private String orderedName;

    private ZonedDateTime orderedAt;

    private String recipientName;

    private String recipientPhone;

    private int deliveryPrice;

    public static OrderGroupResponse fromEntity(OrderGroup OrderGroup){
        OrderGroupResponse dto = new OrderGroupResponse();

        dto.setId(OrderGroup.getId());
        dto.setUserId(OrderGroup.getUserId());
        dto.setWrappingId(OrderGroup.getWrappingId());
        dto.setDeliveryInfoId(OrderGroup.getDeliveryInfoId());
        dto.setOrderedName(OrderGroup.getOrderedName());
        dto.setOrderedAt(OrderGroup.getOrderedAt());
        dto.setRecipientName(OrderGroup.getRecipientName());
        dto.setRecipientPhone(OrderGroup.getRecipientPhone());
        dto.setDeliveryPrice(OrderGroup.getDeliveryPrice());

        return dto;
    }

}
