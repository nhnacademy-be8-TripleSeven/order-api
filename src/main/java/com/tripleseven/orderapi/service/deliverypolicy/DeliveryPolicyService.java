package com.tripleseven.orderapi.service.deliverypolicy;

import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequest;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyResponse;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequest;

public interface DeliveryPolicyService {

    DeliveryPolicyResponse getDeliveryPolicy(Long id);

    DeliveryPolicyResponse createDeliveryPolicy(DeliveryPolicyCreateRequest deliveryPolicyCreateRequest);

    DeliveryPolicyResponse updateDeliveryPolicy(Long id, DeliveryPolicyUpdateRequest deliveryPolicyUpdateRequest);

    void deleteDeliveryPolicy(Long id);

}
