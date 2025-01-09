package com.tripleseven.orderapi.service.defaultdeliverypolicy;

import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DefaultDeliveryPolicy;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;
import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.tripleseven.orderapi.exception.notfound.DeliveryPolicyNotFoundException;
import com.tripleseven.orderapi.repository.defaultdeliverypolicy.DefaultDeliveryPolicyRepository;
import com.tripleseven.orderapi.repository.defaultdeliverypolicy.querydsl.QueryDslDefaultDeliveryPolicyRepository;
import com.tripleseven.orderapi.repository.deliverypolicy.DeliveryPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultDeliveryPolicyServiceImpl implements DefaultDeliveryPolicyService {
    private final QueryDslDefaultDeliveryPolicyRepository queryDslDeliveryPolicyRepository;
    private final DefaultDeliveryPolicyRepository defaultDeliveryPolicyRepository;
    private final DeliveryPolicyRepository deliveryPolicyRepository;

    @Override
    public DefaultDeliveryPolicyDTO getDefaultDeliveryDTO(DeliveryPolicyType deliveryPolicyType) {
        return queryDslDeliveryPolicyRepository.findDefaultDeliveryPolicyByType(deliveryPolicyType);
    }

    @Override
    public Long updateDefaultDelivery(DefaultDeliveryPolicyUpdateRequestDTO request) {
        DefaultDeliveryPolicy defaultDeliveryPolicy = defaultDeliveryPolicyRepository.findDefaultDeliveryPolicyByDeliveryPolicyType(request.getDeliveryPolicyType());

        Optional<DeliveryPolicy> optionalDeliveryPolicy = deliveryPolicyRepository.findById(request.getDeliveryPolicyId());
        if (optionalDeliveryPolicy.isEmpty()) {
            throw new DeliveryPolicyNotFoundException(request.getDeliveryPolicyId());
        }
        DeliveryPolicy deliveryPolicy = optionalDeliveryPolicy.get();

        defaultDeliveryPolicy.ofUpdate(
                deliveryPolicy
        );

        return defaultDeliveryPolicy.getId();
    }

}
