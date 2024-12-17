package com.example.orderapi.service.pointhistory;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
import com.example.orderapi.entity.pointhistory.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointHistoryService {
    Page<PointHistoryResponse> getPointHistoriesByMemberId(Long memberId, Pageable pageable);
    Page<PointHistoryResponse> getPointHistories(Pageable pageable);
    void removePointHistoryById(Long pointHistoryId);
    void removePointHistoriesByMemberId(Long memberId);
    PointHistoryResponse getPointHistory(Long pointHistoryId);
    PointHistoryResponse createPointHistory(PointHistoryCreateRequest request);
    Integer getTotalPointByMemberId(Long memberId);

}
