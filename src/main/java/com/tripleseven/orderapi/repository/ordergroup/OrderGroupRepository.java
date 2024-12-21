package com.tripleseven.orderapi.repository.ordergroup;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.repository.ordergroup.querydsl.QueryDslOrderGroupRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderGroupRepository extends JpaRepository<OrderGroup, Long>, QueryDslOrderGroupRepository {

    List<OrderGroup> findAllByUserIdOrderByIdAsc(Long userId, Pageable pageable);
}
