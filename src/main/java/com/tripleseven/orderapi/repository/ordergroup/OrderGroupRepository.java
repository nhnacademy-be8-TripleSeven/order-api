package com.tripleseven.orderapi.repository.ordergroup;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.repository.ordergroup.querydsl.QueryDslOrderGroupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;

public interface OrderGroupRepository extends JpaRepository<OrderGroup, Long>, QueryDslOrderGroupRepository {

    List<OrderGroup> findAllByUserIdOrderByIdAsc(Long userId, Pageable pageable);

    @Query("SELECT og FROM OrderGroup og WHERE og.userId = :userId " +
            "AND (:startTime IS NULL OR og.orderedAt >= :startTime) " +
            "AND (:endTime IS NULL OR og.orderedAt <= :endTime)")
    Page<OrderGroup> findAllByPeriod(Long userId, ZonedDateTime startTime, ZonedDateTime endTime, Pageable pageable);
}
