package com.tripleseven.orderapi.service.deliverypolicy;

import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequest;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyResponse;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequest;
import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.tripleseven.orderapi.repository.deliverypolicy.DeliveryPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class DeliveryPolicyServiceImpl implements DeliveryPolicyService {

    private final DeliveryPolicyRepository deliveryPolicyRepository;

    @Override
    public DeliveryPolicyResponse getDeliveryPolicy(Long id) {
        Optional<DeliveryPolicy> optionalDeliveryPolicy = deliveryPolicyRepository.findById(id);
        if (optionalDeliveryPolicy.isEmpty()) {
            throw new RuntimeException();
        }
        return DeliveryPolicyResponse.fromEntity(optionalDeliveryPolicy.get());
    }

    @Override
    public DeliveryPolicyResponse createDeliveryPolicy(DeliveryPolicyCreateRequest deliveryPolicyCreateRequest) {
        DeliveryPolicy deliveryPolicy = new DeliveryPolicy();
        deliveryPolicy.ofCreate(deliveryPolicyCreateRequest.getName(), deliveryPolicyCreateRequest.getPrice());
        DeliveryPolicy createDeliveryPolicy = deliveryPolicyRepository.save(deliveryPolicy);
        return DeliveryPolicyResponse.fromEntity(createDeliveryPolicy);
    }

    @Override
    public DeliveryPolicyResponse updateDeliveryPolicy(Long id, DeliveryPolicyUpdateRequest deliveryPolicyUpdateRequest) {
        Optional<DeliveryPolicy> optionalDeliveryPolicy = deliveryPolicyRepository.findById(id);

        if (optionalDeliveryPolicy.isEmpty()) {
            throw new RuntimeException();
        }

        DeliveryPolicy deliveryPolicy = optionalDeliveryPolicy.get();
        deliveryPolicy.ofUpdate(deliveryPolicyUpdateRequest.getName(), deliveryPolicyUpdateRequest.getPrice());

        return DeliveryPolicyResponse.fromEntity(deliveryPolicy);
    }

    @Override
    public void deleteDeliveryPolicy(Long id) {
        if(!deliveryPolicyRepository.existsById(id)){
            throw new RuntimeException();
        }
        deliveryPolicyRepository.deleteById(id);
    }
}
