package com.example.orderapi.service;

import com.example.orderapi.entity.PointHistory.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointHistoryService {
    Page<PointHistory> findByMemberId(Long memberId, Pageable pageable);
    Page<PointHistory> findAll(Pageable pageable);
    void deleteByPointHistoryId(Long pointHistoryId);
    void deleteByMemberId(Long memberId);
    PointHistory findByPointHistoryId(Long pointHistoryId);
}
