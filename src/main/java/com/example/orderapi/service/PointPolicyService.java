package com.example.orderapi.service;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.example.orderapi.dto.pointpolicy.PointPolicyCreateRequest;
import com.example.orderapi.dto.pointpolicy.PointPolicyResponse;
import com.example.orderapi.dto.pointpolicy.PointPolicyUpdateRequest;
import com.example.orderapi.entity.PointPolicy.PointPolicy;

import java.util.List;

public interface PointPolicyService {
    PointPolicyResponse findById(Long id);
    PointPolicyResponse save(PointPolicyCreateRequest request);
    void update(Long id,PointPolicyUpdateRequest request);
    void delete(Long id);
    List<PointPolicyResponse> findAll();

}
