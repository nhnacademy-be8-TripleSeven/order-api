package com.tripleseven.orderapi.service.pointpolicy;

import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyCreateRequest;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyResponse;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyUpdateRequest;

import java.util.List;

public interface PointPolicyService {
    PointPolicyResponse findById(Long id);
    PointPolicyResponse save(PointPolicyCreateRequest request);
    PointPolicyResponse update(Long id,PointPolicyUpdateRequest request);
    void delete(Long id);
    List<PointPolicyResponse> findAll();

}
