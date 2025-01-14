package com.tripleseven.orderapi.service.deliverypolicy;

import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyResponseDTO;
import com.tripleseven.orderapi.dto.deliverypolicy.DeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.deliverypolicy.DeliveryPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class DeliveryPolicyServiceImpl implements DeliveryPolicyService {

    private final DeliveryPolicyRepository deliveryPolicyRepository;

    @Override
    public DeliveryPolicyResponseDTO getDeliveryPolicy(Long id) {
        DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

        return DeliveryPolicyResponseDTO.fromEntity(deliveryPolicy);
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
        DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

        deliveryPolicy.ofUpdate(deliveryPolicyUpdateRequestDTO.getName(), deliveryPolicyUpdateRequestDTO.getPrice());

        return DeliveryPolicyResponseDTO.fromEntity(deliveryPolicy);
    }

    @Override
    public void deleteDeliveryPolicy(Long id) {
        if (!deliveryPolicyRepository.existsById(id)) {
            throw new CustomException(ErrorCode.ID_NOT_FOUND);
        }
        deliveryPolicyRepository.deleteById(id);
    }

    @Override
    public List<DeliveryPolicyResponseDTO> getAllDeliveryPolicies() {
        List<DeliveryPolicy> deliveryPolicies = deliveryPolicyRepository.findAll();

        List<DeliveryPolicyResponseDTO> responses = new ArrayList<>();

        if (responses.isEmpty()) {
            return List.of();
        }
        for (DeliveryPolicy deliveryPolicy : deliveryPolicies) {
            responses.add(DeliveryPolicyResponseDTO.fromEntity(deliveryPolicy));
        }

        return responses;
    }
}
