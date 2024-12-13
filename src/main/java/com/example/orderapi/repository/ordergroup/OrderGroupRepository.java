package com.example.orderapi.repository.ordergroup;

import com.example.orderapi.entity.OrderGroup;
import com.example.orderapi.repository.ordergroup.querydsl.QueryDslOrderGroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderGroupRepository extends JpaRepository<OrderGroup, Long>, QueryDslOrderGroupRepository {

//    List<OrderGroup> findAllByUserIdOrderByIdAsc(Long userId, Pageable pageable);
}
