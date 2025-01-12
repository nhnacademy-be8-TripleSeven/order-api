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

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultPointPolicyServiceImpl implements DefaultPointPolicyService {
    private final QueryDslDefaultPointPolicyRepository queryDslDefaultPointPolicyRepository;
    private final DefaultPointPolicyRepository defaultPointPolicyRepository;
    private final PointPolicyRepository pointPolicyRepository;

    @Override
    @Transactional(readOnly = true)
    public DefaultPointPolicyDTO getDefaultPointPolicyDTO(PointPolicyType type) {
        return queryDslDefaultPointPolicyRepository.findDefaultPointPolicyByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DefaultPointPolicyDTO> getDefaultPointPolicies() {
        List<DefaultPointPolicyDTO> defaultPointPolicies = queryDslDefaultPointPolicyRepository.findDefaultPointPolicies();
        if (Objects.isNull(defaultPointPolicies)) {
            return List.of();
        }
        return defaultPointPolicies;
    }


    @Override
    @Transactional
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
            defaultPointPolicy = defaultPointPolicyRepository.save(defaultPointPolicy);
        } else {
            defaultPointPolicy.ofUpdate(
                    pointPolicy
            );
        }

        return defaultPointPolicy.getId();
    }

    @Override
    @Transactional(readOnly = true)
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

    @Override
    @Transactional
    public DefaultPointPolicyDTO createRegisterPointHistory(Long memberId){
        DefaultPointPolicyDTO dto = queryDslDefaultPointPolicyRepository.findDefaultPointPolicyByType(PointPolicyType.REGISTER);

        if(Objects.isNull(dto)){
            PointPolicy pointPolicy = new PointPolicy();
            pointPolicy.ofCreate("회원 가입 정책", 5000, BigDecimal.ZERO);

            PointPolicy savedPointPolicy = pointPolicyRepository.save(pointPolicy);

            DefaultPointPolicy defaultPointPolicy = new DefaultPointPolicy();
            defaultPointPolicy.ofCreate(PointPolicyType.REGISTER, savedPointPolicy);

            DefaultPointPolicy savedDefault = defaultPointPolicyRepository.save(defaultPointPolicy);
            dto = new DefaultPointPolicyDTO(
                    savedDefault.getId(),
                    savedDefault.getPointPolicyType(),
                    savedDefault.getPointPolicy()
            );
        }

        return dto;
    }

}
