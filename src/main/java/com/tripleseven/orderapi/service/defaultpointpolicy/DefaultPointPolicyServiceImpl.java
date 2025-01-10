package com.tripleseven.orderapi.service.defaultpointpolicy;

import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.defaultpointpolicy.DefaultPointPolicy;
import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.notfound.PointHistoryNotFoundException;
import com.tripleseven.orderapi.repository.defaultpointpolicy.DefaultPointPolicyRepository;
import com.tripleseven.orderapi.repository.defaultpointpolicy.querydsl.QueryDslDefaultPointPolicyRepository;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultPointPolicyServiceImpl implements DefaultPointPolicyService {
    private final QueryDslDefaultPointPolicyRepository queryDslDefaultPointPolicyRepository;
    private final DefaultPointPolicyRepository defaultPointPolicyRepository;
    private final PointPolicyRepository pointPolicyRepository;

    @Override
    public DefaultPointPolicyDTO getDefaultPointPolicyDTO(PointPolicyType type) {
        return queryDslDefaultPointPolicyRepository.findDefaultPointPolicyByType(type);
    }

    @Override
    public List<DefaultPointPolicyDTO> getDefaultPointPolicies() {
        List<DefaultPointPolicyDTO> defaultPointPolicies = queryDslDefaultPointPolicyRepository.findDefaultPointPolicies();
        if (Objects.isNull(defaultPointPolicies)) {
            return List.of();
        }
        return defaultPointPolicies;
    }


    @Override
    public Long updateDefaultPoint(DefaultPointPolicyUpdateRequestDTO request) {
        DefaultPointPolicy defaultPointPolicy = defaultPointPolicyRepository.findDefaultPointPolicyByPointPolicyType(request.getType());

        Optional<PointPolicy> optionalPointPolicy = pointPolicyRepository.findById(request.getPointPolicyId());

        if (optionalPointPolicy.isEmpty()) {
            throw new PointHistoryNotFoundException("not found id: " + request.getPointPolicyId());
        }

        PointPolicy pointPolicy = optionalPointPolicy.get();

        if (Objects.isNull(defaultPointPolicy)) {
            defaultPointPolicy = new DefaultPointPolicy();
            defaultPointPolicy.ofCreate(
                    request.getType(),
                    pointPolicy
            );
        } else {
            defaultPointPolicy.ofUpdate(
                    pointPolicy
            );
        }

        return defaultPointPolicy.getId();
    }

    @Override
    public DefaultPointPolicyDTO getDefaultPointPolicy(PointPolicyType type){
        DefaultPointPolicy defaultPointPolicy = defaultPointPolicyRepository.findDefaultPointPolicyByPointPolicyType(type);

        return new DefaultPointPolicyDTO(
                defaultPointPolicy.getId(),
                defaultPointPolicy.getPointPolicyType(),
                defaultPointPolicy.getPointPolicy().getId(),
                defaultPointPolicy.getPointPolicy().getName(),
                defaultPointPolicy.getPointPolicy().getAmount(),
                defaultPointPolicy.getPointPolicy().getRate()
        );
    }

}
