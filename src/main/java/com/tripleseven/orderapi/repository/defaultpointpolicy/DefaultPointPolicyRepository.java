package com.tripleseven.orderapi.repository.defaultpointpolicy;

import com.tripleseven.orderapi.entity.defaultpointpolicy.DefaultPointPolicy;
import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultPointPolicyRepository extends JpaRepository<DefaultPointPolicy, Long> {
    DefaultPointPolicy findDefaultPointPolicyByPointPolicyType(PointPolicyType pointPolicyType);
}
