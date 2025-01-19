package com.tripleseven.orderapi.repository.querydsl;

import com.tripleseven.orderapi.dto.order.OrderViewDTO;
import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.orderdetail.OrderDetailRepository;
import com.tripleseven.orderapi.repository.orderdetail.querydsl.QueryDslOrderDetailRepositoryImpl;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Transactional
@Import(QueryDslOrderDetailRepositoryImpl.class)
class QueryDslOrderDetailTest {

    @Autowired
    private WrappingRepository wrappingRepository;

    @Autowired
    private OrderGroupRepository orderGroupRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private QueryDslOrderDetailRepositoryImpl queryDslOrderDetailRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setup() {

        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate("Test Wrapping", 100);
        Wrapping savedWrapping = wrappingRepository.save(wrapping);

        OrderGroup orderGroup1 = new OrderGroup();
        orderGroup1.ofCreate(1L, "Test User 1", "Recipient 1", "01012345678", null, 1000, "Address 1", savedWrapping);
        OrderGroup savedOrderGroup1 = orderGroupRepository.save(orderGroup1);

        OrderGroup orderGroup2 = new OrderGroup();
        orderGroup2.ofCreate(1L, "Test User 2", "Recipient 2", "01087654321", null, 1500, "Address 2", savedWrapping);
        OrderGroup savedOrderGroup2 = orderGroupRepository.save(orderGroup2);

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.ofCreate(1L, 100, 1000, 900, savedOrderGroup1);
        orderDetailRepository.save(orderDetail1);

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.ofCreate(2L, 200, 2000, 1800, savedOrderGroup2);
        orderDetailRepository.save(orderDetail2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindAllByPeriodAndUserId() {

        List<OrderViewDTO> results = queryDslOrderDetailRepository.findAllByPeriodAndUserId(
                1L,
                LocalDate.now().minusDays(5),
                LocalDate.now(),
                OrderStatus.PAYMENT_PENDING
        );


        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Recipient 2", results.get(0).getRecipientName());
        assertEquals(1800, results.get(0).getPrice());
    }

    @Test
    void testFindAllByPeriod() {
        List<OrderViewDTO> results = queryDslOrderDetailRepository.findAllByPeriod(
                LocalDate.now().minusDays(5),
                LocalDate.now(),
                null
        );

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Recipient 2", results.get(0).getRecipientName());
        assertEquals(1800, results.get(0).getPrice());
    }

    @Test
    void testComputeNetTotal() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();

        Long result = queryDslOrderDetailRepository.computeNetTotal(userId, startDate, endDate);

        // 1000 * 100 - 900 + 2000 * 200 - 1800
        assertEquals(497300, result);
    }

    @Test
    void testComputeNetTotal_EmptyResult() {
        Long userId = 2L; // 존재하지 않는 사용자 ID
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();

        Long result = queryDslOrderDetailRepository.computeNetTotal(userId, startDate, endDate);

        // 결과가 없을 경우 0이 반환되어야 함
        assertEquals(0, result);
    }
}