package com.tripleseven.orderapi.repository;

import com.tripleseven.orderapi.entity.deliverypolicy.DeliveryPolicy;
import com.tripleseven.orderapi.repository.deliverypolicy.DeliveryPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DeliveryPolicyRepositoryTest {
    @Autowired
    private DeliveryPolicyRepository deliveryPolicyRepository;

    private DeliveryPolicy deliveryPolicy;

    private DeliveryPolicy savedDeliveryPolicy;

    @BeforeEach
    void setUp() {
        deliveryPolicy = new DeliveryPolicy();
        deliveryPolicy.ofCreate("Test DeliveryPolicy", 1000);
        savedDeliveryPolicy = deliveryPolicyRepository.save(deliveryPolicy);

    }

    @Test
    void testSaveDeliveryPolicy() {

        assertNotNull(savedDeliveryPolicy.getId());
        assertEquals("Test DeliveryPolicy", savedDeliveryPolicy.getName());
        assertEquals(1000, savedDeliveryPolicy.getPrice());
    }

    @Test
    void testFindById() {
        Optional<DeliveryPolicy> foundDeliveryPolicy = deliveryPolicyRepository.findById(savedDeliveryPolicy.getId());

        assertTrue(foundDeliveryPolicy.isPresent());
        assertEquals("Test DeliveryPolicy", foundDeliveryPolicy.get().getName());
        assertEquals(1000, foundDeliveryPolicy.get().getPrice());
    }

    @Test
    void testFindById_NotFound() {
        Optional<DeliveryPolicy> foundDeliveryPolicy = deliveryPolicyRepository.findById(999L);
        assertFalse(foundDeliveryPolicy.isPresent());
    }

    @Test
    void testDeleteDeliveryPolicy() {
        deliveryPolicyRepository.delete(savedDeliveryPolicy);

        Optional<DeliveryPolicy> foundDeliveryPolicy = deliveryPolicyRepository.findById(savedDeliveryPolicy.getId());
        assertFalse(foundDeliveryPolicy.isPresent());
    }

}
