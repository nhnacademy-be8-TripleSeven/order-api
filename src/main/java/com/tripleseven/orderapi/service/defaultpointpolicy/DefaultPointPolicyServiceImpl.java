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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultPointPolicyServiceImpl implements DefaultPointPolicyService {
    private final QueryDslDefaultPointPolicyRepository queryDslDefaultPointPolicyRepository;
    private final DefaultPointPolicyRepository defaultPointPolicyRepository;
    private final PointPolicyRepository pointPolicyRepository;

    @Override
    public DefaultPointPolicyDTO getDefaultPointPolicyDTO(PointPolicyType pointPolicyType) {
        return queryDslDefaultPointPolicyRepository.findDefaultPointPolicyByType(pointPolicyType);
    }

    @Override
    public List<DefaultPointPolicyDTO> getDefaultPointPolicies() {
        List<DefaultPointPolicyDTO> defaultPointPolicies = queryDslDefaultPointPolicyRepository.findDefaultPointPolicies();

        return defaultPointPolicies;
    }



    @Override
    public Long updateDefaultPoint(DefaultPointPolicyUpdateRequestDTO request) {
        DefaultPointPolicy defaultPointPolicy = defaultPointPolicyRepository.findDefaultPointPolicyByPointPolicyType(request.getPointPolicyType());

        Optional<PointPolicy> optionalPointPolicy = pointPolicyRepository.findById(request.getPointPolicyId());

        if (optionalPointPolicy.isEmpty()) {
            throw new PointHistoryNotFoundException("not found id: " + request.getPointPolicyId());
        }

        PointPolicy pointPolicy = optionalPointPolicy.get();

        defaultPointPolicy.ofUpdate(
                pointPolicy
        );

        return defaultPointPolicy.getId();
    }


}
