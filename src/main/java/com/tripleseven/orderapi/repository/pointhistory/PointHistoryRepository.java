package com.tripleseven.orderapi.repository.pointhistory;

import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    Page<PointHistory> findAllByMemberId(Long memberId, Pageable pageable);
    void deleteAllByMemberId(Long memberId);
    @Query("select SUM(p.amount) from PointHistory p where p.memberId = ?1")
    int sumAmount(Long memberId);
}
