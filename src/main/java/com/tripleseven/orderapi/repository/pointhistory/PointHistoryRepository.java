package com.tripleseven.orderapi.repository.pointhistory;

import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    Page<PointHistory> findAllByMemberId(Long memberId, Pageable pageable);
    void deleteAllByMemberId(Long memberId);
    @Query("select COALESCE(SUM(p.amount), 0) from PointHistory p where p.memberId = ?1")
    Integer sumAmount(Long memberId);
    @Query("SELECT ph FROM PointHistory ph WHERE ph.memberId = :memberId AND ph.changedAt >= :startDate AND ph.changedAt < :endDate")
    Page<PointHistory> findAllByChangedAtBetween(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    Page<PointHistory> findAllByMemberIdAndTypes(Long memberId, HistoryTypes types, Pageable pageable);
}

