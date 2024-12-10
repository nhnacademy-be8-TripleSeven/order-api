package com.example.orderapi.dto.ordergroup;

import com.example.orderapi.entity.OrderGroup;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class OrderGroupResponse {
    private Long id;

    private Long userId;

    private Long wrappingId;

    private Long deliveryInfoId;

    private ZonedDateTime orderedAt;

    private String recipientName;

    private String recipientPhone;

    private int deliveryPrice;

    public static OrderGroupResponse fromEntity(OrderGroup orderGroup){
        OrderGroupResponse dto = new OrderGroupResponse();

        dto.setId(orderGroup.getId());
        dto.setUserId(orderGroup.getUserId());
        dto.setWrappingId(orderGroup.getWrappingId());
        dto.setDeliveryInfoId(orderGroup.getDeliveryInfoId());
        dto.setOrderedAt(orderGroup.getOrderedAt());
        dto.setRecipientName(orderGroup.getRecipientName());
        dto.setRecipientPhone(orderGroup.getRecipientPhone());
        dto.setDeliveryPrice(orderGroup.getDeliveryPrice());

        return dto;
    }

}
