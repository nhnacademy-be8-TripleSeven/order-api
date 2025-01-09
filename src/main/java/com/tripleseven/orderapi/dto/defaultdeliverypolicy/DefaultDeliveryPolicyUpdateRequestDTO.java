package com.tripleseven.orderapi.dto.defaultdeliverypolicy;

import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;
import lombok.Value;

@Value
public class DefaultDeliveryPolicyUpdateRequestDTO {
    Long deliveryPolicyId;
    DeliveryPolicyType deliveryPolicyType;
}
