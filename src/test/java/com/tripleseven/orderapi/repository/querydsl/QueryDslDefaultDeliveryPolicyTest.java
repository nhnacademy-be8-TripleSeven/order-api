package com.tripleseven.orderapi.repository.querydsl;

import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DefaultDeliveryPolicy;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DeliveryPolicyType;
import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.tripleseven.orderapi.repository.defaultdeliverypolicy.DefaultDeliveryPolicyRepository;
import com.tripleseven.orderapi.repository.defaultdeliverypolicy.querydsl.QueryDslDefaultDeliveryPolicyRepositoryImpl;
import com.tripleseven.orderapi.repository.deliverypolicy.DeliveryPolicyRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Transactional
@Import(QueryDslDefaultDeliveryPolicyRepositoryImpl.class)
class QueryDslDefaultDeliveryPolicyTest {

    @Autowired
    private QueryDslDefaultDeliveryPolicyRepositoryImpl queryDslDefaultDeliveryPolicyRepository;

    @Autowired
    private DeliveryPolicyRepository deliveryPolicyRepository;

    @Autowired
    private DefaultDeliveryPolicyRepository defaultDeliveryPolicyRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findDefaultDeliveryPolicy_shouldReturnCorrectPolicies() {
        DeliveryPolicy deliveryPolicy1 = new DeliveryPolicy();
        deliveryPolicy1.ofCreate("Standard Shipping", 3000);
        DeliveryPolicy savedPolicy1 = deliveryPolicyRepository.save(deliveryPolicy1);

        DeliveryPolicy deliveryPolicy2 = new DeliveryPolicy();
        deliveryPolicy2.ofCreate("Express Shipping", 5000);
        DeliveryPolicy savedPolicy2 = deliveryPolicyRepository.save(deliveryPolicy2);

        DefaultDeliveryPolicy defaultPolicy1 = new DefaultDeliveryPolicy();
        defaultPolicy1.ofCreate(DeliveryPolicyType.DEFAULT, savedPolicy1);
        defaultDeliveryPolicyRepository.save(defaultPolicy1);

        DefaultDeliveryPolicy defaultPolicy2 = new DefaultDeliveryPolicy();
        defaultPolicy2.ofCreate(DeliveryPolicyType.EVENT, savedPolicy2);
        defaultDeliveryPolicyRepository.save(defaultPolicy2);

        entityManager.flush();
        entityManager.clear();

        List<DefaultDeliveryPolicyDTO> result = queryDslDefaultDeliveryPolicyRepository.findDefaultDeliveryPolicy();

        assertNotNull(result);
        assertEquals(2, result.size());

        DefaultDeliveryPolicyDTO policy1 = result.get(0);
        assertEquals("Standard Shipping", policy1.getName());
        assertEquals(3000, policy1.getPrice());
        assertEquals(DeliveryPolicyType.DEFAULT, policy1.getType());

        DefaultDeliveryPolicyDTO policy2 = result.get(1);
        assertEquals("Express Shipping", policy2.getName());
        assertEquals(5000, policy2.getPrice());
        assertEquals(DeliveryPolicyType.EVENT, policy2.getType());
    }
}
