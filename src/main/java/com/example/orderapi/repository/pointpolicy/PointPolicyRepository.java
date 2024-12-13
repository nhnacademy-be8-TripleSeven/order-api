package com.example.orderapi.repository.pointpolicy;

import com.example.orderapi.entity.PointPolicy.PointPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
}
