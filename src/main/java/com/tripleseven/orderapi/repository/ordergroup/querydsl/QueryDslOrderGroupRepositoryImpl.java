package com.tripleseven.orderapi.repository.ordergroup.querydsl;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.ordergroup.QOrderGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class QueryDslOrderGroupRepositoryImpl extends QuerydslRepositorySupport implements QueryDslOrderGroupRepository {

    public QueryDslOrderGroupRepositoryImpl() {
        super(OrderGroup.class);
    }

    @Override
    public Page<OrderGroup> findAllByUserId(Long userId, Pageable pageable) {
        QOrderGroup orderGroup = QOrderGroup.orderGroup;

        List<OrderGroup> list = from(orderGroup)
                .where(orderGroup.userId.eq(userId))
                .orderBy(orderGroup.id.desc())
                .limit(pageable.getPageSize())
                .fetch();

        long total = from(orderGroup)
                .where(orderGroup.userId.eq(userId))
                .fetchCount();

        return new PageImpl<>(list, pageable, total);
    }
}
