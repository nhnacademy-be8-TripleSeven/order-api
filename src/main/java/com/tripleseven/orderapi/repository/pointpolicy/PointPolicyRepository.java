package com.tripleseven.orderapi.repository.pointpolicy;

import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
}
