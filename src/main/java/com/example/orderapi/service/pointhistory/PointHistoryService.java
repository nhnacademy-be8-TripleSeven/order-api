package com.example.orderapi.service.pointhistory;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
import com.example.orderapi.entity.pointhistory.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointHistoryService {
    Page<PointHistoryResponse> getMemberPointHistory(Long memberId, Pageable pageable);
    Page<PointHistory> getAllPointHistories(Pageable pageable);
    void removePointHistoryById(Long pointHistoryId);
    void removeAllPointHistoriesForMember(Long memberId);
    PointHistoryResponse getPointHistory(Long pointHistoryId);
    PointHistoryResponse createPointHistory(PointHistoryCreateRequest request);
    PointHistoryResponse assignPointBasedOnPolicy(Long policyId, Long memberId);
    Integer calculateTotalPoints(Long pointId);
}
