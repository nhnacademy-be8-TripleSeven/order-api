package com.tripleseven.orderapi.dto.deliverypolicy;

import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.tripleseven.orderapi.common.environmentutils.EnvironmentUtil;
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
        if (!EnvironmentUtil.isTestEnvironment() && Objects.isNull(id)) {
            log.error("DeliveryPolicy id cannot be null");
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

