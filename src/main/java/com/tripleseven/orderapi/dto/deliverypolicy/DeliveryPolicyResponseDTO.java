package com.tripleseven.orderapi.dto.deliverypolicy;

import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Getter
@Slf4j
public class DeliveryPolicyResponseDTO {
    private final Long id;

    private final String name;

    private final int price;


    @Builder
    private DeliveryPolicyResponseDTO(Long id, String name, int price) {
        if (Objects.isNull(id)) {
            log.error("DeliveryPolicy id cannot be null");
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public static DeliveryPolicyResponseDTO fromEntity(DeliveryPolicy deliveryPolicy) {
        return DeliveryPolicyResponseDTO.builder()
                .id(deliveryPolicy.getId())
                .name(deliveryPolicy.getName())
                .price(deliveryPolicy.getPrice())
                .build();
    }
}

