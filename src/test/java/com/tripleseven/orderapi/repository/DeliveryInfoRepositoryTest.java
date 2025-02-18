package com.tripleseven.orderapi.repository;

import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DeliveryInfoRepositoryTest {
    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    private OrderGroupRepository orderGroupRepository;

    @Autowired
    private WrappingRepository wrappingRepository;

    private DeliveryInfo deliveryInfo;

    private LocalDate arrivedAt;

    @BeforeEach
    void setUp() {
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate("Test Wrapping", 100);
        Wrapping savedWrapping = wrappingRepository.save(wrapping);
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", "01012345678", 1000, "Test Address", savedWrapping);
        OrderGroup savedOrderGroup = orderGroupRepository.save(orderGroup);
        deliveryInfo = new DeliveryInfo();
        deliveryInfo.ofCreate(savedOrderGroup, LocalDate.now());
        arrivedAt = LocalDate.parse("2024-12-17");
    }

    @Test
    void testSaveDeliveryInfo() {
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);

        assertNotNull(savedDeliveryInfo.getId());
        assertNotNull(savedDeliveryInfo.getArrivedAt());
    }

    @Test
    void testFindById() {
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);

        Optional<DeliveryInfo> foundDeliveryInfo = deliveryInfoRepository.findById(savedDeliveryInfo.getId());

        assertTrue(foundDeliveryInfo.isPresent());

        DeliveryInfo getDeliveryInfo = foundDeliveryInfo.get();
        assertNotNull(getDeliveryInfo.getArrivedAt());
    }

    @Test
    void testFindById_NotFound() {
        Optional<DeliveryInfo> foundDeliveryInfo = deliveryInfoRepository.findById(999L);
        assertFalse(foundDeliveryInfo.isPresent());
    }

    @Test
    void testDeleteDeliveryInfo() {
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);
        assertNotNull(savedDeliveryInfo.getId());
        deliveryInfoRepository.delete(savedDeliveryInfo);

        Optional<DeliveryInfo> foundDeliveryInfo = deliveryInfoRepository.findById(savedDeliveryInfo.getId());
        assertFalse(foundDeliveryInfo.isPresent());
    }

    @Test
    void testUpdateArrivedDeliveryInfo() {
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);

        savedDeliveryInfo.ofUpdate("Test DeliveryInfo", 12345678, arrivedAt);

        DeliveryInfo updatedDeliveryInfo = deliveryInfoRepository.save(savedDeliveryInfo);

        assertEquals(arrivedAt, updatedDeliveryInfo.getArrivedAt());
    }

    @Test
    void testExistsById() {
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);

        boolean exists = deliveryInfoRepository.existsById(savedDeliveryInfo.getId());

        assertTrue(exists);
    }

}
