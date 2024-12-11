package com.example.orderapi.service.impl;

import com.example.orderapi.entity.PointPolicy.PointPolicy;
import com.example.orderapi.repository.PointPolicyRepository;
import com.example.orderapi.service.PointPolicyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Transactional
@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {

    private final PointPolicyRepository pointPolicyRepository;



    @Override
    public PointPolicy findById(Long id) {
        return pointPolicyRepository.findById(id).orElse(null);
    }

    @Override
    public PointPolicy save(PointPolicy pointPolicy) {
        return pointPolicyRepository.save(pointPolicy);
    }

    @Override
    public void update(PointPolicy pointPolicy) {
        pointPolicyRepository.save(pointPolicy);
    }

    @Override
    public void delete(Long id) {
        pointPolicyRepository.deleteById(id);
    }

    @Override
    public List<PointPolicy> findAll() {
        return pointPolicyRepository.findAll();
    }
}
