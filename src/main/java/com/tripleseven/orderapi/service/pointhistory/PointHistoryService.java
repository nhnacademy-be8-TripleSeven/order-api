package com.tripleseven.orderapi.service.pointhistory;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponse;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PointHistoryService {
    Page<PointHistoryResponse> getPointHistoriesByMemberId(Long memberId, Pageable pageable);
    Page<PointHistoryResponse> getPointHistories(Pageable pageable);
    void removePointHistoryById(Long pointHistoryId);
    void removePointHistoriesByMemberId(Long memberId);
    PointHistoryResponse getPointHistory(Long pointHistoryId);
    PointHistoryResponse createPointHistory(Long memberId, PointHistoryCreateRequest request);
    Integer getTotalPointByMemberId(Long memberId);
    Page<PointHistoryResponse> getPointHistoriesWithinPeriod(Long memberId, LocalDate startDate, LocalDate endDate, String sortDirection, Pageable pageable);
    Page<PointHistoryResponse> getPointHistoriesWithState(Long memberId, HistoryTypes state, Pageable pageable);

}
