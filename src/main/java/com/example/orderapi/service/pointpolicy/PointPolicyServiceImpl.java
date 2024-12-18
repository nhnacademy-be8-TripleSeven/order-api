package com.example.orderapi.service.pointpolicy;

import com.example.orderapi.dto.pointpolicy.PointPolicyCreateRequest;
import com.example.orderapi.dto.pointpolicy.PointPolicyResponse;
import com.example.orderapi.dto.pointpolicy.PointPolicyUpdateRequest;
import com.example.orderapi.entity.pointpolicy.PointPolicy;
import com.example.orderapi.exception.notfound.PointPolicyNotFoundException;
import com.example.orderapi.repository.pointpolicy.PointPolicyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Transactional
@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;



    @Override
    public PointPolicyResponse findById(Long id) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(id).orElse(null);
        if(Objects.isNull(pointPolicy)) {
            throw new PointPolicyNotFoundException("PointPolicyId="+id+" not found");
        }
        return PointPolicyResponse.fromEntity(pointPolicy);
    }

    @Override
    public PointPolicyResponse save(PointPolicyCreateRequest request) {
        PointPolicy pointPolicy = new PointPolicy(
                null,
                request.getName(),
                request.getAmount(),
                request.getRate()
        );
        PointPolicy savedPolicy = pointPolicyRepository.save(pointPolicy);
        return PointPolicyResponse.fromEntity(savedPolicy);
    }

    @Override
    public PointPolicyResponse update(Long id, PointPolicyUpdateRequest request) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(id).orElse(null);
        if(Objects.isNull(pointPolicy)) {
            throw new PointPolicyNotFoundException("PointPolicyId="+id+" not found");
        }
        pointPolicy.setName(request.getName());
        pointPolicy.setRate(request.getRate());
        pointPolicy.setAmount(request.getAmount());
        PointPolicy savedPolicy = pointPolicyRepository.save(pointPolicy);
        return PointPolicyResponse.fromEntity(savedPolicy);
    }

    @Override
    public void delete(Long id) {
        pointPolicyRepository.deleteById(id);
    }

    @Override
    public List<PointPolicyResponse> findAll() {
        List<PointPolicy> pointPolicies = pointPolicyRepository.findAll();
        if(pointPolicies.isEmpty()) {
            throw new PointPolicyNotFoundException("pointPolicy not found");
        }
        List<PointPolicyResponse> pointPolicyResponseList = new ArrayList<>();
        for(PointPolicy pointPolicy : pointPolicies) {
            pointPolicyResponseList.add(PointPolicyResponse.fromEntity(pointPolicy));
        }
        return pointPolicyResponseList;
    }
}
