package com.example.orderapi.service.deliverypolicy;

import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequest;
import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyResponse;
import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequest;

public interface DeliveryPolicyService {

    DeliveryPolicyResponse getById(Long id);

    DeliveryPolicyResponse create(DeliveryPolicyCreateRequest deliveryPolicyCreateRequest);

    DeliveryPolicyResponse update(Long id, DeliveryPolicyUpdateRequest deliveryPolicyUpdateRequest);

    void delete(Long id);

}
