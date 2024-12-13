package com.example.orderapi.service.deliverypolicy;

import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequest;
import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyResponse;
import com.example.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequest;
import com.example.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.example.orderapi.repository.deliverypolicy.DeliveryPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
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
        deliveryPolicy.ofCreate();
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
        deliveryPolicy.update(deliveryPolicyUpdateRequest.getName(), deliveryPolicyUpdateRequest.getPrice());

        return DeliveryPolicyResponse.fromEntity(deliveryPolicy);
    }

    @Override
    public void delete(Long id) {
        if(!deliveryPolicyRepository.existsById(id)){
            throw new RuntimeException();
        }
        deliveryPolicyRepository.deleteById(id);
    }
}
