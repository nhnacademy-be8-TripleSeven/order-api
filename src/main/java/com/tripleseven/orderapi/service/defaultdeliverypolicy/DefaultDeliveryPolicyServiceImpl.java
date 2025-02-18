package com.tripleseven.orderapi.service.defaultdeliverypolicy;

import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DefaultDeliveryPolicy;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;
import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.defaultdeliverypolicy.DefaultDeliveryPolicyRepository;
import com.tripleseven.orderapi.repository.defaultdeliverypolicy.querydsl.QueryDslDefaultDeliveryPolicyRepository;
import com.tripleseven.orderapi.repository.deliverypolicy.DeliveryPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultDeliveryPolicyServiceImpl implements DefaultDeliveryPolicyService {
    private final QueryDslDefaultDeliveryPolicyRepository queryDslDeliveryPolicyRepository;
    private final DefaultDeliveryPolicyRepository defaultDeliveryPolicyRepository;
    private final DeliveryPolicyRepository deliveryPolicyRepository;

    @Override
    public List<DefaultDeliveryPolicyDTO> getDefaultDeliveryDTO() {
        return queryDslDeliveryPolicyRepository.findDefaultDeliveryPolicy();
    }

    @Override
    public Long updateDefaultDelivery(DefaultDeliveryPolicyUpdateRequestDTO request) {
        DefaultDeliveryPolicy defaultDeliveryPolicy = defaultDeliveryPolicyRepository.findDefaultDeliveryPolicyByDeliveryPolicyType(request.getType());

        DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findById(request.getDeliveryPolicyId())
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

        if (Objects.isNull(defaultDeliveryPolicy)) {
            defaultDeliveryPolicy = new DefaultDeliveryPolicy();
            defaultDeliveryPolicy.ofCreate(
                    request.getType(),
                    deliveryPolicy
            );
            defaultDeliveryPolicy = defaultDeliveryPolicyRepository.save(defaultDeliveryPolicy);
        } else {
            defaultDeliveryPolicy.ofUpdate(
                    deliveryPolicy
            );
        }

        return defaultDeliveryPolicy.getId();
    }

    @Override
    public DefaultDeliveryPolicyDTO getDefaultDeliveryPolicy(DeliveryPolicyType type) {
        DefaultDeliveryPolicy defaultDeliveryPolicy = defaultDeliveryPolicyRepository.findDefaultDeliveryPolicyByDeliveryPolicyType(type);

        return new DefaultDeliveryPolicyDTO(
                defaultDeliveryPolicy.getId(),
                defaultDeliveryPolicy.getDeliveryPolicy().getName(),
                defaultDeliveryPolicy.getDeliveryPolicy().getMinPrice(),
                defaultDeliveryPolicy.getDeliveryPolicy().getPrice(),
                defaultDeliveryPolicy.getDeliveryPolicyType()
        );
    }

}
