package com.tripleseven.orderapi.repository.querydsl;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

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


    @Test
    void testFindTotalAmountByOrderGroupId() {
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate("Test Wrapping", 100);
        Wrapping savedWrapping = wrappingRepository.save(wrapping);

        OrderGroup orderGroup = new OrderGroup();
        orderGroup.ofCreate(1L,
                "Test Ordered",
                "Test Recipient",
                "01012345678",
                "01012345678",
                1000,
                "Test Address",
                savedWrapping);
        OrderGroup savedOrderGroup = orderGroupRepository.save(orderGroup);

        PointHistory pointHistory1 = createPointHistory(HistoryTypes.EARN, 500);

        PointHistory pointHistory2 = createPointHistory(HistoryTypes.EARN, 300);

        createOrderGroupPointHistory(pointHistory1, savedOrderGroup);
        createOrderGroupPointHistory(pointHistory2, savedOrderGroup);

        entityManager.flush();
        entityManager.clear();

        Long totalAmount = queryDslOrderGroupPointHistoryRepository.findTotalAmountByOrderGroupId(savedOrderGroup.getId(), HistoryTypes.EARN);

        assertNotNull(totalAmount);
        assertEquals(800, totalAmount);
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