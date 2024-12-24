package com.tripleseven.orderapi.service.pointpolicy;

import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyCreateRequestDTO;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyResponseDTO;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyUpdateRequestDTO;

import java.util.List;

public interface PointPolicyService {
    PointPolicyResponseDTO findById(Long id);
    PointPolicyResponseDTO save(PointPolicyCreateRequestDTO request);
    PointPolicyResponseDTO update(Long id, PointPolicyUpdateRequestDTO request);
    void delete(Long id);
    List<PointPolicyResponseDTO> findAll();

}
