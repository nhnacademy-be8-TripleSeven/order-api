package com.tripleseven.orderapi.repository;

import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class OrderGroupRepositoryTest {

    @Autowired
    private OrderGroupRepository orderGroupRepository;

    @Autowired
    private WrappingRepository wrappingRepository;

    private OrderGroup orderGroup;

    @BeforeEach
    void setUp() {
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate("Test Wrapping", 100);
        wrappingRepository.save(wrapping);

        orderGroup = new OrderGroup();
        orderGroup.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", 1000, wrapping);
    }

    @Test
    void testSaveOrderGroup() {
        OrderGroup savedOrderGroup = orderGroupRepository.save(orderGroup);

        assertNotNull(savedOrderGroup.getId());
        assertEquals("Test Ordered", savedOrderGroup.getOrderedName());
        assertEquals("Test Recipient", savedOrderGroup.getRecipientName());
        assertEquals("01012345678", savedOrderGroup.getRecipientPhone());
        assertEquals(1000, savedOrderGroup.getDeliveryPrice());
        assertEquals("Test Wrapping", savedOrderGroup.getWrapping().getName());
        assertEquals(100, savedOrderGroup.getWrapping().getPrice());
        assertNull(savedOrderGroup.getDeliveryInfo());
    }

    @Test
    void testFindById() {
        OrderGroup savedOrderGroup = orderGroupRepository.save(orderGroup);

        Optional<OrderGroup> foundOrderGroup = orderGroupRepository.findById(savedOrderGroup.getId());

        assertTrue(foundOrderGroup.isPresent());
        assertEquals("Test Ordered", foundOrderGroup.get().getOrderedName());
        assertEquals("Test Recipient", foundOrderGroup.get().getRecipientName());
        assertEquals("01012345678", foundOrderGroup.get().getRecipientPhone());
        assertEquals(1000, foundOrderGroup.get().getDeliveryPrice());
        assertEquals("Test Wrapping", foundOrderGroup.get().getWrapping().getName());
        assertEquals(100, foundOrderGroup.get().getWrapping().getPrice());
        assertNull(foundOrderGroup.get().getDeliveryInfo());
    }

    @Test
    void testFindById_NotFound() {
        Optional<OrderGroup> foundOrderGroup = orderGroupRepository.findById(999L);
        assertFalse(foundOrderGroup.isPresent());
    }

    @Test
    void testDeleteOrderGroup() {
        OrderGroup savedOrderGroup = orderGroupRepository.save(orderGroup);

        assertNotNull(savedOrderGroup.getId());
        orderGroupRepository.delete(savedOrderGroup);

        Optional<OrderGroup> foundOrderGroup = orderGroupRepository.findById(savedOrderGroup.getId());
        assertFalse(foundOrderGroup.isPresent());
    }

    @Test
    void testDeleteOrderGroup_Fail() {
        assertThrows(RuntimeException.class, () -> orderGroupRepository.deleteById(orderGroup.getId()));
    }

    @Test
    void testUpdateDeliveryInfoOrderGroup() {
        OrderGroup savedOrderGroup = orderGroupRepository.save(orderGroup);
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.ofCreate("Test DeliveryInfo", 12345678);
        savedOrderGroup.ofUpdateDeliveryInfo(deliveryInfo);
        OrderGroup updatedOrderGroup = orderGroupRepository.save(savedOrderGroup);

        assertEquals("Test DeliveryInfo", updatedOrderGroup.getDeliveryInfo().getName());
        assertEquals(12345678, updatedOrderGroup.getDeliveryInfo().getInvoiceNumber());

        assertEquals(savedOrderGroup.getId(), updatedOrderGroup.getId());
        assertEquals(savedOrderGroup.getOrderedName(), updatedOrderGroup.getOrderedName());
        assertEquals(savedOrderGroup.getRecipientName(), updatedOrderGroup.getRecipientName());
        assertEquals(savedOrderGroup.getRecipientPhone(), updatedOrderGroup.getRecipientPhone());
        assertEquals(savedOrderGroup.getDeliveryPrice(), updatedOrderGroup.getDeliveryPrice());
        assertEquals(savedOrderGroup.getWrapping().getName(), updatedOrderGroup.getWrapping().getName());
        assertEquals(savedOrderGroup.getWrapping().getPrice(), updatedOrderGroup.getWrapping().getPrice());
        assertEquals(savedOrderGroup.getDeliveryInfo().getId(), updatedOrderGroup.getDeliveryInfo().getId());
    }

    @Test
    void testExistsById() {
        OrderGroup savedOrderGroup = orderGroupRepository.save(orderGroup);
        boolean exists = orderGroupRepository.existsById(savedOrderGroup.getId());

        assertTrue(exists);
    }





}
