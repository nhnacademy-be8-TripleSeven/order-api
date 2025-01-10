package com.tripleseven.orderapi.service.deliverypolicy;

import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyResponseDTO;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequestDTO;

import java.util.List;

public interface DeliveryPolicyService {

    DeliveryPolicyResponseDTO getDeliveryPolicy(Long id);

    DeliveryPolicyResponseDTO createDeliveryPolicy(DeliveryPolicyCreateRequestDTO deliveryPolicyCreateRequestDTO);

    DeliveryPolicyResponseDTO updateDeliveryPolicy(Long id, DeliveryPolicyUpdateRequestDTO deliveryPolicyUpdateRequestDTO);

    void deleteDeliveryPolicy(Long id);


    List<DeliveryPolicyResponseDTO> getAllDeliveryPolicies();
}
