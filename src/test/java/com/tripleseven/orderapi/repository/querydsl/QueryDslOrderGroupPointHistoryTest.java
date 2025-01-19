package com.tripleseven.orderapi.repository.querydsl;

import com.tripleseven.orderapi.dto.pointhistory.UserPointHistoryDTO;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.ordergrouppointhistory.OrderGroupPointHistory;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.ordergrouppointhistory.OrderGroupPointHistoryRepository;
import com.tripleseven.orderapi.repository.ordergrouppointhistory.querydsl.QueryDslOrderGroupPointHistoryRepository;
import com.tripleseven.orderapi.repository.ordergrouppointhistory.querydsl.QueryDslOrderGroupPointHistoryRepositoryImpl;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@DataJpaTest
@Import(QueryDslOrderGroupPointHistoryRepositoryImpl.class)
class QueryDslOrderGroupPointHistoryTest {

    @Autowired
    private QueryDslOrderGroupPointHistoryRepository queryDslOrderGroupPointHistoryRepository;

    @Autowired
    private WrappingRepository wrappingRepository;

    @Autowired
    private OrderGroupRepository orderGroupRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private OrderGroupPointHistoryRepository orderGroupPointHistoryRepository;

    @Autowired
    private EntityManager entityManager;

    private Long memberId;

    private OrderGroup orderGroup;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate("Test Wrapping", 100);
        Wrapping savedWrapping = wrappingRepository.save(wrapping);

        orderGroup = new OrderGroup();
        orderGroup.ofCreate(1L,
                "Test Ordered",
                "Test Recipient",
                "01012345678",
                "01012345678",
                1000,
                "Test Address",
                savedWrapping);
        orderGroup = orderGroupRepository.save(orderGroup);

        PointHistory pointHistory1 = createPointHistory(HistoryTypes.EARN, 500);

        PointHistory pointHistory2 = createPointHistory(HistoryTypes.EARN, 300);

        createOrderGroupPointHistory(pointHistory1, orderGroup);
        createOrderGroupPointHistory(pointHistory2, orderGroup);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindTotalAmountByOrderGroupId() {

        Long totalAmount = queryDslOrderGroupPointHistoryRepository.findTotalAmountByOrderGroupId(orderGroup.getId(), HistoryTypes.EARN);

        assertNotNull(totalAmount);
        assertEquals(800, totalAmount);
    }

    @Test
    void testFindUserPointHistories() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime endDate = LocalDateTime.now();

        Page<UserPointHistoryDTO> page = queryDslOrderGroupPointHistoryRepository.findUserPointHistories(
                memberId, startDate, endDate, pageable);

        assertNotNull(page);
        assertEquals(2, page.getTotalElements());

        List<UserPointHistoryDTO> results = page.getContent();

        assertEquals(300, results.get(0).getAmount());
        assertEquals(HistoryTypes.EARN, results.get(0).getTypes());
        assertEquals(orderGroup.getId(), results.get(0).getOrderGroupId());

        assertEquals(500, results.get(1).getAmount());
        assertEquals(HistoryTypes.EARN, results.get(1).getTypes());
        assertEquals(orderGroup.getId(), results.get(1).getOrderGroupId());
    }

    private PointHistory createPointHistory(HistoryTypes type, int amount) {
        PointHistory pointHistory = PointHistory.ofCreate(type, amount, "Test Comment", 1L);
        return pointHistoryRepository.save(pointHistory);
    }

    private void createOrderGroupPointHistory(PointHistory pointHistory, OrderGroup orderGroup) {
        OrderGroupPointHistory history = new OrderGroupPointHistory();
        history.ofCreate(pointHistory, orderGroup);
        orderGroupPointHistoryRepository.save(history);
    }
}