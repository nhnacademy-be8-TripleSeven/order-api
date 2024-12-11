package com.example.orderapi.service;

import com.example.orderapi.entity.PointPolicy.PointPolicy;

import java.util.List;

public interface PointPolicyService {
    PointPolicy findById(Long id);
    PointPolicy save(PointPolicy pointPolicy);
    void update(PointPolicy pointPolicy);
    void delete(Long id);
    List<PointPolicy> findAll();

}
