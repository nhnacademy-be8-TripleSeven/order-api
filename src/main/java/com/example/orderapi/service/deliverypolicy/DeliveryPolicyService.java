package com.example.orderapi.service.deliverypolicy;

import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequest;
import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyResponse;
import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequest;

public interface DeliveryPolicyService {

    DeliveryPolicyResponse getDeliveryPolicy(Long id);

    DeliveryPolicyResponse createDeliveryPolicy(DeliveryPolicyCreateRequest deliveryPolicyCreateRequest);

    DeliveryPolicyResponse update(Long id, DeliveryPolicyUpdateRequest deliveryPolicyUpdateRequest);

    void delete(Long id);

}
