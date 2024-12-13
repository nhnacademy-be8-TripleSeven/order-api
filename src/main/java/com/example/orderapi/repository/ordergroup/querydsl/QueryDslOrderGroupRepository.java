package com.example.orderapi.repository.ordergroup.querydsl;

import com.example.orderapi.entity.OrderGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryDslOrderGroupRepository {
    Page<OrderGroup> findAllByUserId(Long userId, Pageable pageable);
}
