package com.example.orderapi.service.pointhistory;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
import com.example.orderapi.entity.pointhistory.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointHistoryService {
    Page<PointHistoryResponse> findByMemberId(Long memberId, Pageable pageable);
    Page<PointHistory> findAll(Pageable pageable);
    void deleteByPointHistoryId(Long pointHistoryId);
    void deleteByMemberId(Long memberId);
    PointHistoryResponse findByPointHistoryId(Long pointHistoryId);
    PointHistoryResponse save(PointHistoryCreateRequest request);
    PointHistoryResponse save(Long policyId, Long memberId);
    Integer getPoint(Long pointId);
}
