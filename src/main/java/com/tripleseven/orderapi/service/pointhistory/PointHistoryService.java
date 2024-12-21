package com.tripleseven.orderapi.service.pointhistory;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponse;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
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
