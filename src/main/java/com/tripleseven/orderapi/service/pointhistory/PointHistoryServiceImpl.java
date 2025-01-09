package com.tripleseven.orderapi.service.pointhistory;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryPageResponseDTO;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryCreateRequestDTO;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;
import com.tripleseven.orderapi.dto.pointhistory.UserPointHistoryDTO;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.notfound.OrderGroupNotFoundException;
import com.tripleseven.orderapi.exception.notfound.PointHistoryNotFoundException;
import com.tripleseven.orderapi.exception.notfound.PointPolicyNotFoundException;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.ordergrouppointhistory.QueryDslOrderGroupPointHistoryRepository;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicyRepository pointPolicyRepository;
    private final OrderGroupRepository orderGroupRepository;
    private final QueryDslOrderGroupPointHistoryRepository queryDslOrderGroupPointHistoryRepository;

    @Override
    public Page<PointHistoryResponseDTO> getPointHistoriesByMemberId(Long memberId, Pageable pageable) {
        Page<PointHistory> histories = pointHistoryRepository.findAllByMemberId(memberId, pageable);

        if (histories.isEmpty()) {
            throw new PointHistoryNotFoundException(String.format("No point histories found for memberId=%d", memberId));
        }

        return histories.map(PointHistoryResponseDTO::fromEntity);
    }

    @Override
    public Page<PointHistoryResponseDTO> getPointHistories(Pageable pageable) {
        Page<PointHistory> histories = pointHistoryRepository.findAll(pageable);

        if (histories.getContent().isEmpty()) {
            throw new PointHistoryNotFoundException("No point histories found.");
        }

        return histories.map(PointHistoryResponseDTO::fromEntity);
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
    public PointHistoryResponseDTO getPointHistory(Long pointHistoryId) {
        PointHistory history = pointHistoryRepository.findById(pointHistoryId)
                .orElseThrow(() -> new PointHistoryNotFoundException(
                        String.format("PointHistory with id=%d not found", pointHistoryId)
                ));

        return PointHistoryResponseDTO.fromEntity(history);
    }

    @Override
    public PointHistoryResponseDTO createPointHistory(Long memberId, PointHistoryCreateRequestDTO request) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(request.getPointPolicyId())
                .orElseThrow(() -> new PointPolicyNotFoundException(
                        String.format("PointPolicy with id=%d not found", request.getPointPolicyId())
                ));
        OrderGroup orderGroup = orderGroupRepository.findById(request.getOrderGroupId())
                .orElseThrow(() -> new OrderGroupNotFoundException(request.getOrderGroupId()));

        PointHistory pointHistory = PointHistory.ofCreate(
                request.getTypes(),
                pointPolicy.getAmount(),
                pointPolicy.getName(),
                memberId
        );

        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);
        return PointHistoryResponseDTO.fromEntity(savedHistory);
    }

    @Override
    public Integer getTotalPointByMemberId(Long memberId) {
        return pointHistoryRepository.sumAmount(memberId);
    }

    public Page<PointHistoryResponseDTO> getPointHistoriesWithinPeriod(Long memberId, LocalDate startDate, LocalDate endDate, String sortDirection, Pageable pageable) {
        LocalDateTime startDateTime = startDate.atStartOfDay();         // 시작일 00:00:00
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay(); // 종료일의 다음날 00:00:00

        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, "changedAt"));

        Page<PointHistory> histories = pointHistoryRepository.findAllByChangedAtBetween(memberId, startDateTime, endDateTime, sortedPageable);
        if (histories.isEmpty()) {
            throw new PointHistoryNotFoundException("No point histories found.");
        }

        return histories.map(PointHistoryResponseDTO::fromEntity);
    }

    @Override
    public Page<PointHistoryResponseDTO> getPointHistoriesWithState(Long memberId, HistoryTypes state, Pageable pageable) {
        Page<PointHistory> histories = pointHistoryRepository.findAllByMemberIdAndTypes(memberId, state, pageable);
        if (histories.isEmpty()) {
            throw new PointHistoryNotFoundException("No point histories found.");
        }
        return histories.map(PointHistoryResponseDTO::fromEntity);
    }

    @Override
    public int getUsedPoint(Long orderGroupId) {
        Integer amount = queryDslOrderGroupPointHistoryRepository.findTotalAmountByOrderGroupId(orderGroupId, HistoryTypes.SPEND);
        return Objects.isNull(amount) ? 0 : amount;
    }

    @Override
    public int getEarnedPoint(Long orderGroupId) {
        Integer amount = queryDslOrderGroupPointHistoryRepository.findTotalAmountByOrderGroupId(orderGroupId, HistoryTypes.EARN);
        return Objects.isNull(amount) ? 0 : amount;
    }


    @Override
    public PointHistoryPageResponseDTO<UserPointHistoryDTO> getUserPointHistories(Long memberId, String startDate, String endDate, Pageable pageable) {
        // 날짜 변환
        LocalDateTime start = (startDate != null && !startDate.trim().isEmpty())
                ? LocalDate.parse(startDate).atStartOfDay()
                : LocalDateTime.MIN;
        LocalDateTime end = (endDate != null && !endDate.trim().isEmpty())
                ? LocalDate.parse(endDate).plusDays(1).atStartOfDay()
                : LocalDateTime.now();

        // QueryDSL 메서드 호출
        Page<UserPointHistoryDTO> page = queryDslOrderGroupPointHistoryRepository.findUserPointHistories(memberId, start, end, pageable);

        // PageResponseDTO로 변환하여 반환
        return new PointHistoryPageResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

}