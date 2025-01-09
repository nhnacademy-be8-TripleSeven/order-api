package com.tripleseven.orderapi.repository.ordergrouppointhistory;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.tripleseven.orderapi.dto.pointhistory.QUserPointHistoryDTO;
import com.tripleseven.orderapi.dto.pointhistory.UserPointHistoryDTO;
import com.tripleseven.orderapi.entity.ordergroup.QOrderGroup;
import com.tripleseven.orderapi.entity.ordergrouppointhistory.OrderGroupPointHistory;
import com.tripleseven.orderapi.entity.ordergrouppointhistory.QOrderGroupPointHistory;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.QPointHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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




    @Override
    public Page<UserPointHistoryDTO> findUserPointHistories(Long memberId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        QPointHistory pointHistory = QPointHistory.pointHistory;
        QOrderGroupPointHistory orderGroupPointHistory = QOrderGroupPointHistory.orderGroupPointHistory;
        QOrderGroup orderGroup = QOrderGroup.orderGroup;

        // QueryDSL Query 생성
        JPAQuery<UserPointHistoryDTO> baseQuery = new JPAQuery<>(entityManager)
                .select(new QUserPointHistoryDTO(
                        pointHistory.id,
                        pointHistory.amount,
                        pointHistory.changedAt,
                        pointHistory.types,
                        pointHistory.comment,
                        orderGroup.id
                ))
                .from(pointHistory)
                .leftJoin(pointHistory.orderGroupPointHistories, orderGroupPointHistory)
                .leftJoin(orderGroupPointHistory.orderGroup, orderGroup)
                .where(
                        pointHistory.memberId.eq(memberId)
                                .and(pointHistory.changedAt.goe(startDate))
                                .and(pointHistory.changedAt.lt(endDate))
                )
                .orderBy(pointHistory.changedAt.desc());

        // FetchResults 사용
        QueryResults<UserPointHistoryDTO> results = baseQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        // Total Count와 Content 분리
        long total = results.getTotal();
        List<UserPointHistoryDTO> content = results.getResults();


        // 결과 반환
        return new PageImpl<>(content, pageable, total);
    }



}
