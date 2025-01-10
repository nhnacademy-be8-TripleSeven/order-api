package com.tripleseven.orderapi.service.defaultdeliverypolicy;

import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;

import java.util.List;

public interface DefaultDeliveryPolicyService {
    List<DefaultDeliveryPolicyDTO> getDefaultDeliveryDTO();

    Long updateDefaultDelivery(DefaultDeliveryPolicyUpdateRequestDTO request);

    DefaultDeliveryPolicyDTO getDefaultDeliveryPolicy(DeliveryPolicyType type);
}
