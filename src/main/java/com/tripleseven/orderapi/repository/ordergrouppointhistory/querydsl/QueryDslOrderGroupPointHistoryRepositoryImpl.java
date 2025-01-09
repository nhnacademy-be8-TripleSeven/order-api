package com.tripleseven.orderapi.repository.ordergrouppointhistory.querydsl;

import com.querydsl.jpa.impl.JPAQuery;
import com.tripleseven.orderapi.entity.ordergroup.QOrderGroup;
import com.tripleseven.orderapi.entity.ordergrouppointhistory.OrderGroupPointHistory;
import com.tripleseven.orderapi.entity.ordergrouppointhistory.QOrderGroupPointHistory;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.QPointHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class QueryDslOrderGroupPointHistoryRepositoryImpl extends QuerydslRepositorySupport implements QueryDslOrderGroupPointHistoryRepository {
    public QueryDslOrderGroupPointHistoryRepositoryImpl() {
        super(OrderGroupPointHistory.class);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer findTotalAmountByOrderGroupId(Long orderGroupId, HistoryTypes historyTypes) {
        QPointHistory pointHistory = QPointHistory.pointHistory;
        QOrderGroupPointHistory orderGroupPointHistory = QOrderGroupPointHistory.orderGroupPointHistory;
        QOrderGroup orderGroup = QOrderGroup.orderGroup;

        return new JPAQuery<>(entityManager)
                .select(pointHistory.amount)
                .from(pointHistory)
                .join(pointHistory.orderGroupPointHistories, orderGroupPointHistory)
                .join(orderGroupPointHistory.orderGroup, orderGroup)
                .where(orderGroup.id.eq(orderGroupId)
                        .and(pointHistory.types.eq(historyTypes)))
                .fetchOne();
    }
}
