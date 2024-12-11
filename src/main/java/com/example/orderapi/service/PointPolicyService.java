package com.example.orderapi.service;

import com.example.orderapi.entity.PointPolicy;

import java.util.List;

public interface PointPolicyService {
    PointPolicy findById(Long id);
    PointPolicy save(PointPolicy pointPolicy);
    PointPolicy update(PointPolicy pointPolicy);
    void delete(Long id);
    PointPolicy findByName(String name);
    List<PointPolicy> findAll();
}
