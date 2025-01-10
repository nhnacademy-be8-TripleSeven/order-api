package com.tripleseven.orderapi.repository.deliverypolicy;

import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryPolicyRepository extends JpaRepository<DeliveryPolicy, Long> {
}
