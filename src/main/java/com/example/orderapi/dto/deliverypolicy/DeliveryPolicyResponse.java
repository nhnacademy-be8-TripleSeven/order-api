package com.example.orderapi.dto.deliverypolicy;

import com.example.orderapi.entity.deliverypolicy.DeliveryPolicy;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Getter
@Slf4j
public class DeliveryPolicyResponse {
    private final Long id;

    private final String name;

    private final int price;


    @Builder
    private DeliveryPolicyResponse(Long id, String name, int price) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public static DeliveryPolicyResponse fromEntity(DeliveryPolicy deliveryPolicy) {
        return DeliveryPolicyResponse.builder()
                .id(deliveryPolicy.getId())
                .name(deliveryPolicy.getName())
                .price(deliveryPolicy.getPrice())
                .build();
    }
}

