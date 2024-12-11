package com.example.orderapi.repository;

import com.example.orderapi.entity.PointHistory.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    Page<PointHistory> findAllByMemberId(Long memberId, Pageable pageable);
    void deleteAllByMemberId(Long memberId);
}
