package com.tripleseven.orderapi.repository.defaultdeliverypolicy.querydsl;

import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;

public interface QueryDslDefaultDeliveryPolicyRepository {
    DefaultDeliveryPolicyDTO findDefaultDeliveryPolicyByType(DeliveryPolicyType deliveryPolicyType);
}
