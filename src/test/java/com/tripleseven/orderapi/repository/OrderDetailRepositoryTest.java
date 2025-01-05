package com.tripleseven.orderapi.repository;

import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.orderdetail.OrderDetailRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class OrderDetailRepositoryTest {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private WrappingRepository wrappingRepository;

    @Autowired
    private OrderGroupRepository orderGroupRepository;

    private OrderDetail orderDetail;

    @BeforeEach
    void setUp() {
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate("Test Wrapping", 100);
        wrappingRepository.save(wrapping);

        OrderGroup orderGroup = new OrderGroup();
        orderGroup.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", 1000, "Test Address", wrapping);
        orderGroupRepository.save(orderGroup);

        orderDetail = new OrderDetail();
        orderDetail.ofCreate(1L, 3, 10000, 9000, orderGroup);
    }

    @Test
    void testSaveOrderDetail() {
        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);

        assertNotNull(savedOrderDetail.getId());
        assertEquals(1L, savedOrderDetail.getBookId());
        assertEquals(3, savedOrderDetail.getAmount());
        assertEquals(10000, savedOrderDetail.getPrimePrice());
        assertEquals(9000, savedOrderDetail.getDiscountPrice());
        assertEquals(1000, savedOrderDetail.getOrderGroup().getDeliveryPrice());
        assertEquals("Test Ordered", savedOrderDetail.getOrderGroup().getOrderedName());
        assertEquals("Test Recipient", savedOrderDetail.getOrderGroup().getRecipientName());
        assertEquals("01012345678", savedOrderDetail.getOrderGroup().getRecipientPhone());
        assertEquals("Test Wrapping", savedOrderDetail.getOrderGroup().getWrapping().getName());
        assertEquals(100, savedOrderDetail.getOrderGroup().getWrapping().getPrice());
    }

    @Test
    void testFindById() {
        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);

        Optional<OrderDetail> foundOrderDetail = orderDetailRepository.findById(savedOrderDetail.getId());

        assertTrue(foundOrderDetail.isPresent());
        assertEquals(1L, savedOrderDetail.getBookId());
        assertEquals(3, savedOrderDetail.getAmount());
        assertEquals(10000, savedOrderDetail.getPrimePrice());
        assertEquals(9000, savedOrderDetail.getDiscountPrice());
        assertEquals(1000, savedOrderDetail.getOrderGroup().getDeliveryPrice());
        assertEquals("Test Ordered", savedOrderDetail.getOrderGroup().getOrderedName());
        assertEquals("Test Recipient", savedOrderDetail.getOrderGroup().getRecipientName());
        assertEquals("01012345678", savedOrderDetail.getOrderGroup().getRecipientPhone());
        assertEquals("Test Wrapping", savedOrderDetail.getOrderGroup().getWrapping().getName());
        assertEquals(100, savedOrderDetail.getOrderGroup().getWrapping().getPrice());
    }

    @Test
    void testFindById_NotFound() {
        Optional<OrderDetail> foundOrderDetail = orderDetailRepository.findById(999L);
        assertFalse(foundOrderDetail.isPresent());
    }

    @Test
    void testDeleteOrderDetail() {
        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);

        assertNotNull(savedOrderDetail.getId());
        orderDetailRepository.delete(savedOrderDetail);

        Optional<OrderDetail> foundOrderDetail = orderDetailRepository.findById(savedOrderDetail.getId());
        assertFalse(foundOrderDetail.isPresent());
    }

    @Test
    void testDeleteOrderDetail_Fail() {
        assertThrows(RuntimeException.class, () -> orderDetailRepository.deleteById(orderDetail.getId()));
    }

    @Test
    void testUpdateStatusOrderDetail() {
        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);

        OrderStatus orderStatus = OrderStatus.PAYMENT_COMPLETED;
        savedOrderDetail.ofUpdateStatus(orderStatus);
        OrderDetail updatedOrderDetail = orderDetailRepository.save(savedOrderDetail);
        assertEquals(OrderStatus.PAYMENT_COMPLETED, updatedOrderDetail.getOrderStatus());
        assertEquals(savedOrderDetail.getId(), updatedOrderDetail.getId());
        assertEquals(savedOrderDetail.getBookId(), updatedOrderDetail.getBookId());
        assertEquals(savedOrderDetail.getAmount(), updatedOrderDetail.getAmount());
        assertEquals(savedOrderDetail.getPrimePrice(), updatedOrderDetail.getPrimePrice());
        assertEquals(savedOrderDetail.getDiscountPrice(), updatedOrderDetail.getDiscountPrice());
        assertEquals(savedOrderDetail.getOrderGroup().getId(), updatedOrderDetail.getOrderGroup().getId());
    }

    @Test
    void testExistsById() {
        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);
        boolean exists = orderDetailRepository.existsById(savedOrderDetail.getId());
        assertTrue(exists);
    }
}
