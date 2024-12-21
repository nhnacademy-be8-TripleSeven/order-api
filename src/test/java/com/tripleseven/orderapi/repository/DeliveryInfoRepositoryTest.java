package com.tripleseven.orderapi.repository;

import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DeliveryInfoRepositoryTest {
    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository;

    private DeliveryInfo deliveryInfo;

    private ZonedDateTime forwardedAt;

    private LocalDate deliveryDate;

    private ZonedDateTime arrivedAt;

    @BeforeEach
    void setUp() {
        deliveryInfo = new DeliveryInfo();
        deliveryInfo.ofCreate("Test DeliveryInfo", 12345678);
        forwardedAt = ZonedDateTime.parse("2024-12-15T10:30:00+09:00[Asia/Seoul]");
        deliveryDate = LocalDate.parse("2024-12-15");
        arrivedAt = ZonedDateTime.parse("2024-12-17T11:24:00+09:00[Asia/Seoul]");
    }

    @Test
    void testSaveDeliveryInfo() {
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);

        assertNotNull(savedDeliveryInfo.getId());
        assertEquals("Test DeliveryInfo", savedDeliveryInfo.getName());
        assertEquals(12345678, savedDeliveryInfo.getInvoiceNumber());
        assertNull(savedDeliveryInfo.getDeliveryDate());
        assertNull(savedDeliveryInfo.getArrivedAt());
        assertNull(savedDeliveryInfo.getForwardedAt());
    }

    @Test
    void testFindById() {
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);

        Optional<DeliveryInfo> foundDeliveryInfo = deliveryInfoRepository.findById(savedDeliveryInfo.getId());

        assertTrue(foundDeliveryInfo.isPresent());

        DeliveryInfo getDeliveryInfo = foundDeliveryInfo.get();
        assertEquals("Test DeliveryInfo", getDeliveryInfo.getName());
        assertEquals(12345678, getDeliveryInfo.getInvoiceNumber());
        assertNull(getDeliveryInfo.getDeliveryDate());
        assertNull(getDeliveryInfo.getArrivedAt());
        assertNull(getDeliveryInfo.getForwardedAt());
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
    void testUpdateLogisticsDeliveryInfo() {
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);

        savedDeliveryInfo.ofUpdateLogistics(forwardedAt, deliveryDate);

        DeliveryInfo updatedDeliveryInfo = deliveryInfoRepository.save(savedDeliveryInfo);

        assertEquals(forwardedAt, updatedDeliveryInfo.getForwardedAt());
        assertEquals(deliveryDate, updatedDeliveryInfo.getDeliveryDate());
    }

    @Test
    void testUpdateArrivedDeliveryInfo() {
        DeliveryInfo savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);

        savedDeliveryInfo.ofUpdateArrived(arrivedAt);

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
