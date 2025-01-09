package com.tripleseven.orderapi.repository.defaultdeliverypolicy;

import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DefaultDeliveryPolicy;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultDeliveryPolicyRepository extends JpaRepository<DefaultDeliveryPolicy, Long> {
    DefaultDeliveryPolicy findDefaultDeliveryPolicyByDeliveryPolicyType(DeliveryPolicyType deliveryPolicyType);
}
