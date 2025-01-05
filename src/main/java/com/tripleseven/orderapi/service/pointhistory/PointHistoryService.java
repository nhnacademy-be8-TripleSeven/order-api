package com.tripleseven.orderapi.service.pointhistory;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryCreateRequestDTO;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PointHistoryService {
    Page<PointHistoryResponseDTO> getPointHistoriesByMemberId(Long memberId, Pageable pageable);

    Page<PointHistoryResponseDTO> getPointHistories(Pageable pageable);

    void removePointHistoryById(Long pointHistoryId);

    void removePointHistoriesByMemberId(Long memberId);

    PointHistoryResponseDTO getPointHistory(Long pointHistoryId);

    PointHistoryResponseDTO createPointHistory(Long memberId, PointHistoryCreateRequestDTO request);

    Integer getTotalPointByMemberId(Long memberId);

    Page<PointHistoryResponseDTO> getPointHistoriesWithinPeriod(Long memberId, LocalDate startDate, LocalDate endDate, String sortDirection, Pageable pageable);

    Page<PointHistoryResponseDTO> getPointHistoriesWithState(Long memberId, HistoryTypes state, Pageable pageable);

    int getUsedPoint(Long orderGroupId);

    int getEarnedPoint(Long orderId);
}
