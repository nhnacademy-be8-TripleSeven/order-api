package com.tripleseven.orderapi.repository;

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
        orderGroup.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", 1000, "Test Address", wrapping);
    }

    @Test
    void testSaveOrderGroup() {
        OrderGroup savedOrderGroup = orderGroupRepository.save(orderGroup);

        assertNotNull(savedOrderGroup.getId());
        assertEquals("Test Ordered", savedOrderGroup.getOrderedName());
        assertEquals("Test Recipient", savedOrderGroup.getRecipientName());
        assertEquals("01012345678", savedOrderGroup.getRecipientPhone());
        assertEquals(1000, savedOrderGroup.getDeliveryPrice());
        assertEquals("Test Address", savedOrderGroup.getAddress());
        assertEquals("Test Wrapping", savedOrderGroup.getWrapping().getName());
        assertEquals(100, savedOrderGroup.getWrapping().getPrice());
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
        assertEquals("Test Address", savedOrderGroup.getAddress());
        assertEquals("Test Wrapping", foundOrderGroup.get().getWrapping().getName());
        assertEquals(100, foundOrderGroup.get().getWrapping().getPrice());
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
    void testExistsById() {
        OrderGroup savedOrderGroup = orderGroupRepository.save(orderGroup);
        boolean exists = orderGroupRepository.existsById(savedOrderGroup.getId());

        assertTrue(exists);
    }


}
