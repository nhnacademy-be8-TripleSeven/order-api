package com.example.orderapi.repository.deliverypolicy;

import com.example.orderapi.entity.deliverypolicy.DeliveryPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPolicyRepository extends JpaRepository<DeliveryPolicy, Long> {
}
