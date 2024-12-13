package com.example.orderapi.dto.deliverypolicy;

import com.example.orderapi.entity.DeliveryPolicy;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeliveryPolicyResponse {
    Long id;

    String name;

    int price;

    public static DeliveryPolicyResponse fromEntity(DeliveryPolicy deliveryPolicy) {
        DeliveryPolicyResponse dto = new DeliveryPolicyResponse();
        dto.setId(deliveryPolicy.getId());
        dto.setName(deliveryPolicy.getName());
        dto.setPrice(deliveryPolicy.getPrice());
        return dto;
    }
}

