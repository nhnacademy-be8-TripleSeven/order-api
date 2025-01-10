package com.tripleseven.orderapi.service.deliverypolicy;

import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyResponseDTO;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.tripleseven.orderapi.exception.notfound.DeliveryPolicyNotFoundException;
import com.tripleseven.orderapi.repository.deliverypolicy.DeliveryPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class DeliveryPolicyServiceImpl implements DeliveryPolicyService {

    private final DeliveryPolicyRepository deliveryPolicyRepository;

    @Override
    public DeliveryPolicyResponseDTO getDeliveryPolicy(Long id) {
        Optional<DeliveryPolicy> optionalDeliveryPolicy = deliveryPolicyRepository.findById(id);
        if (optionalDeliveryPolicy.isEmpty()) {
            throw new DeliveryPolicyNotFoundException(id);
        }
        return DeliveryPolicyResponseDTO.fromEntity(optionalDeliveryPolicy.get());
    }

    @Override
    public DeliveryPolicyResponseDTO createDeliveryPolicy(DeliveryPolicyCreateRequestDTO deliveryPolicyCreateRequestDTO) {
        DeliveryPolicy deliveryPolicy = new DeliveryPolicy();
        deliveryPolicy.ofCreate(deliveryPolicyCreateRequestDTO.getName(), deliveryPolicyCreateRequestDTO.getPrice());
        DeliveryPolicy createDeliveryPolicy = deliveryPolicyRepository.save(deliveryPolicy);
        return DeliveryPolicyResponseDTO.fromEntity(createDeliveryPolicy);
    }

    @Override
    public DeliveryPolicyResponseDTO updateDeliveryPolicy(Long id, DeliveryPolicyUpdateRequestDTO deliveryPolicyUpdateRequestDTO) {
        Optional<DeliveryPolicy> optionalDeliveryPolicy = deliveryPolicyRepository.findById(id);

        if (optionalDeliveryPolicy.isEmpty()) {
            throw new DeliveryPolicyNotFoundException(id);
        }

        DeliveryPolicy deliveryPolicy = optionalDeliveryPolicy.get();
        deliveryPolicy.ofUpdate(deliveryPolicyUpdateRequestDTO.getName(), deliveryPolicyUpdateRequestDTO.getPrice());

        return DeliveryPolicyResponseDTO.fromEntity(deliveryPolicy);
    }

    @Override
    public void deleteDeliveryPolicy(Long id) {
        if(!deliveryPolicyRepository.existsById(id)){
            throw new DeliveryPolicyNotFoundException(id);
        }
        deliveryPolicyRepository.deleteById(id);
    }

    @Override
    public List<DeliveryPolicyResponseDTO> getAllDeliveryPolicies(){
        List<DeliveryPolicy> deliveryPolicies = deliveryPolicyRepository.findAll();

        List<DeliveryPolicyResponseDTO> responses = new ArrayList<>();
        for(DeliveryPolicy deliveryPolicy: deliveryPolicies){
            responses.add(DeliveryPolicyResponseDTO.fromEntity(deliveryPolicy));
        }
        return responses;
    }
}
