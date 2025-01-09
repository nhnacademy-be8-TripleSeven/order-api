package com.tripleseven.orderapi.dto.defaultdeliverypolicy;

import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultDeliveryPolicyDTO {
    Long id;
    String name;
    Integer price;
    DeliveryPolicyType type;
}
