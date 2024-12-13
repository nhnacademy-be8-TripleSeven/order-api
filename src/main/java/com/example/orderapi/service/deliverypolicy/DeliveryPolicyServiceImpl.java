package com.example.orderapi.service.deliverypolicy;

import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequest;
import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyResponse;
import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequest;
import com.example.orderapi.entity.DeliveryPolicy;
import com.example.orderapi.repository.deliverypolicy.DeliveryPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryPolicyServiceImpl implements DeliveryPolicyService {

    private final DeliveryPolicyRepository deliveryPolicyRepository;

    @Override
    public DeliveryPolicyResponse getById(Long id) {
        Optional<DeliveryPolicy> optionalDeliveryPolicy = deliveryPolicyRepository.findById(id);
        if (optionalDeliveryPolicy.isEmpty()) {
            throw new RuntimeException();
        }
        return DeliveryPolicyResponse.fromEntity(optionalDeliveryPolicy.get());
    }

    @Override
    public DeliveryPolicyResponse create(DeliveryPolicyCreateRequest deliveryPolicyCreateRequest) {
        DeliveryPolicy deliveryPolicy = new DeliveryPolicy();
        deliveryPolicy.setName(deliveryPolicyCreateRequest.getName());
        deliveryPolicy.setPrice(deliveryPolicyCreateRequest.getPrice());
        DeliveryPolicy createDeliveryPolicy = deliveryPolicyRepository.save(deliveryPolicy);
        return DeliveryPolicyResponse.fromEntity(createDeliveryPolicy);
    }

    @Override
    public DeliveryPolicyResponse update(Long id, DeliveryPolicyUpdateRequest deliveryPolicyUpdateRequest) {
        Optional<DeliveryPolicy> optionalDeliveryPolicy = deliveryPolicyRepository.findById(id);
        if (optionalDeliveryPolicy.isEmpty()) {
            throw new RuntimeException();
        }
        DeliveryPolicy deliveryPolicy = optionalDeliveryPolicy.get();
        deliveryPolicy.setName(deliveryPolicyUpdateRequest.getName());
        deliveryPolicy.setPrice(deliveryPolicyUpdateRequest.getPrice());
        DeliveryPolicy updateDeliveryPolicy = deliveryPolicyRepository.save(deliveryPolicy);
        return DeliveryPolicyResponse.fromEntity(updateDeliveryPolicy);
    }

    @Override
    public void delete(Long id) {
        deliveryPolicyRepository.deleteById(id);
    }
}
