package com.tripleseven.orderapi.repository.ordergroup.querydsl;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryDslOrderGroupRepository {
    Page<OrderGroup> findAllByUserId(Long userId, Pageable pageable);
}
