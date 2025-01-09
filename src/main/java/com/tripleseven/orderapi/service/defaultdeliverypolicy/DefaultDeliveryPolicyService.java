package com.tripleseven.orderapi.service.defaultdeliverypolicy;

import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;

public interface DefaultDeliveryPolicyService {
    DefaultDeliveryPolicyDTO getDefaultDeliveryDTO(DeliveryPolicyType deliveryPolicyType);

    Long updateDefaultDelivery(DefaultDeliveryPolicyUpdateRequestDTO request);
}
