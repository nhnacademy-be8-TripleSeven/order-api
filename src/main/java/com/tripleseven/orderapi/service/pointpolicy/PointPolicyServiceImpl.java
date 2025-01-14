package com.tripleseven.orderapi.service.pointpolicy;

import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyCreateRequestDTO;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyResponseDTO;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyUpdateRequestDTO;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
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
    public PointPolicyResponseDTO findById(Long id) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));
        return PointPolicyResponseDTO.fromEntity(pointPolicy);
    }

    // 새로운 정책 저장
    @Override
    public PointPolicyResponseDTO save(PointPolicyCreateRequestDTO request) {
        PointPolicy pointPolicy = new PointPolicy();

        pointPolicy.ofCreate(
                request.getName(),
                request.getAmount(),
                request.getRate()
        );

        PointPolicy savedPolicy = pointPolicyRepository.save(pointPolicy);
        return PointPolicyResponseDTO.fromEntity(savedPolicy);
    }

    // 기존 정책 업데이트
    @Override
    public PointPolicyResponseDTO update(Long id, PointPolicyUpdateRequestDTO request) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

        pointPolicy.ofUpdate(
                request.getName(),
                request.getAmount(),
                request.getRate()
        );

        PointPolicy updatedPolicy = pointPolicyRepository.save(pointPolicy);
        return PointPolicyResponseDTO.fromEntity(updatedPolicy);
    }

    // 정책 삭제
    @Override
    public void delete(Long id) {
        if (!pointPolicyRepository.existsById(id)) {
            throw new CustomException(ErrorCode.ID_NOT_FOUND);
        }
        pointPolicyRepository.deleteById(id);
    }

    // 전체 정책 조회
    @Override
    public List<PointPolicyResponseDTO> findAll() {
        List<PointPolicy> pointPolicies = pointPolicyRepository.findAll();

        if (pointPolicies.isEmpty()) {
            return List.of();
        }

        return pointPolicies.stream()
                .map(PointPolicyResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}