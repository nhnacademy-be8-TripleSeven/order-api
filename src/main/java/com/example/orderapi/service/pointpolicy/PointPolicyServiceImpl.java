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

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;

    // ID로 조회
    @Override
    public PointPolicyResponse findById(Long id) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(id)
                .orElseThrow(() -> new PointPolicyNotFoundException("PointPolicyId=" + id + " not found"));
        return PointPolicyResponse.fromEntity(pointPolicy);
    }

    // 새로운 정책 저장
    @Override
    public PointPolicyResponse save(PointPolicyCreateRequest request) {
        PointPolicy pointPolicy = PointPolicy.ofCreate(request);
        PointPolicy savedPolicy = pointPolicyRepository.save(pointPolicy);
        return PointPolicyResponse.fromEntity(savedPolicy);
    }

    // 기존 정책 업데이트
    @Override
    public PointPolicyResponse update(Long id, PointPolicyUpdateRequest request) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(id)
                .orElseThrow(() -> new PointPolicyNotFoundException("PointPolicyId=" + id + " not found"));

        pointPolicy.ofUpdate(request);
        PointPolicy updatedPolicy = pointPolicyRepository.save(pointPolicy);
        return PointPolicyResponse.fromEntity(updatedPolicy);
    }

    // 정책 삭제
    @Override
    public void delete(Long id) {
        if (!pointPolicyRepository.existsById(id)) {
            throw new PointPolicyNotFoundException("PointPolicyId=" + id + " not found");
        }
        pointPolicyRepository.deleteById(id);
    }

    // 전체 정책 조회
    @Override
    public List<PointPolicyResponse> findAll() {
        List<PointPolicy> pointPolicies = pointPolicyRepository.findAll();

        if (pointPolicies.isEmpty()) {
            throw new PointPolicyNotFoundException("No point policies found");
        }

        return pointPolicies.stream()
                .map(PointPolicyResponse::fromEntity)
                .collect(Collectors.toList());
    }
}