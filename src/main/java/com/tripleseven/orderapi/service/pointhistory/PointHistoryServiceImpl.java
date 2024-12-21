package com.tripleseven.orderapi.service.pointhistory;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponse;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.notfound.PointHistoryNotFoundException;
import com.tripleseven.orderapi.exception.notfound.PointPolicyNotFoundException;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicyRepository pointPolicyRepository;

    @Override
    public Page<PointHistoryResponse> getPointHistoriesByMemberId(Long memberId, Pageable pageable) {
        Page<PointHistory> histories = pointHistoryRepository.findAllByMemberId(memberId, pageable);

        if (histories.isEmpty()) {
            throw new PointHistoryNotFoundException(String.format("No point histories found for memberId=%d", memberId));
        }

        return histories.map(PointHistoryResponse::fromEntity);
    }

    @Override
    public Page<PointHistoryResponse> getPointHistories(Pageable pageable) {
        Page<PointHistory> histories = pointHistoryRepository.findAll(pageable);

        if (histories.isEmpty()) {
            throw new PointHistoryNotFoundException("No point histories found.");
        }

        return histories.map(PointHistoryResponse::fromEntity);
    }

    @Override
    public void removePointHistoryById(Long pointHistoryId) {
        if (!pointHistoryRepository.existsById(pointHistoryId)) {
            throw new PointHistoryNotFoundException(String.format("PointHistory with id=%d not found", pointHistoryId));
        }

        pointHistoryRepository.deleteById(pointHistoryId);
    }

    @Override
    public void removePointHistoriesByMemberId(Long memberId) {
        pointHistoryRepository.deleteAllByMemberId(memberId);
    }

    @Override
    public PointHistoryResponse getPointHistory(Long pointHistoryId) {
        PointHistory history = pointHistoryRepository.findById(pointHistoryId)
                .orElseThrow(() -> new PointHistoryNotFoundException(
                        String.format("PointHistory with id=%d not found", pointHistoryId)
                ));

        return PointHistoryResponse.fromEntity(history);
    }

    @Override
    public PointHistoryResponse createPointHistory(Long memberId, PointHistoryCreateRequest request) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(request.getPointPolicyId())
                .orElseThrow(() -> new PointPolicyNotFoundException(
                        String.format("PointPolicy with id=%d not found", request.getPointPolicyId())
                ));

        PointHistory pointHistory = PointHistory.ofCreate(
                request.getTypes(),
                pointPolicy.getAmount(),
                pointPolicy.getName(),
                memberId
        );

        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);
        return PointHistoryResponse.fromEntity(savedHistory);
    }

    @Override
    public Integer getTotalPointByMemberId(Long memberId) {
        return pointHistoryRepository.sumAmount(memberId);
    }

    public Page<PointHistoryResponse> getPointHistoriesWithinPeriod(Long memberId, LocalDate startDate, LocalDate endDate,String sortDirection, Pageable pageable) {
        LocalDateTime startDateTime = startDate.atStartOfDay();         // 시작일 00:00:00
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay(); // 종료일의 다음날 00:00:00

        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, "changedAt"));

        Page<PointHistory> histories = pointHistoryRepository.findAllByChangedAtBetween(memberId, startDateTime, endDateTime, sortedPageable);
        if (histories.isEmpty()) {
            throw new PointHistoryNotFoundException("No point histories found.");
        }

        return histories.map(PointHistoryResponse::fromEntity);
    }

    @Override
    public Page<PointHistoryResponse> getPointHistoriesWithState(Long memberId, HistoryTypes state, Pageable pageable) {
        Page<PointHistory> histories = pointHistoryRepository.findAllByMemberIdAndTypes(memberId, state, pageable);
        if (histories.isEmpty()) {
            throw new PointHistoryNotFoundException("No point histories found.");
        }
        return histories.map(PointHistoryResponse::fromEntity);
    }

}