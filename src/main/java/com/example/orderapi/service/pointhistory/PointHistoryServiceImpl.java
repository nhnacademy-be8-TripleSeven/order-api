package com.example.orderapi.service.pointhistory;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
import com.example.orderapi.entity.pointhistory.PointHistory;
import com.example.orderapi.entity.pointpolicy.PointPolicy;
import com.example.orderapi.exception.notfound.PointHistoryNotFoundException;
import com.example.orderapi.exception.notfound.PointPolicyNotFoundException;
import com.example.orderapi.repository.pointhistory.PointHistoryRepository;
import com.example.orderapi.repository.pointpolicy.PointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicyRepository pointPolicyRepository;


    @Override
    public Page<PointHistoryResponse> getPointHistoriesByMemberId(Long memberId, Pageable pageable) {
        Page<PointHistory> histories = pointHistoryRepository.findAllByMemberId(memberId, pageable);
        if(histories.isEmpty()){
            throw new PointHistoryNotFoundException(String.format("memberId=%d's pointHistory is null", memberId));        }


        return histories.map(PointHistoryResponse::fromEntity);
    }

    @Override
    public Page<PointHistoryResponse> getPointHistories(Pageable pageable) {
        Page<PointHistory> histories = pointHistoryRepository.findAll(pageable);
        if(histories.isEmpty()){
            throw new PointHistoryNotFoundException("point history is not found");
        }
        return histories.map(PointHistoryResponse::fromEntity);
    }

    @Override
    public void removePointHistoryById(Long pointHistoryId) {
        pointHistoryRepository.deleteById(pointHistoryId);
    }

    @Override
    public void removePointHistoriesByMemberId(Long memberId) {
        pointHistoryRepository.deleteAllByMemberId(memberId);
    }

    @Override
    public PointHistoryResponse getPointHistory(Long pointHistoryId) {
        PointHistory history = pointHistoryRepository.findById(pointHistoryId).orElse(null);
        if(Objects.isNull(history)){
            throw new PointHistoryNotFoundException(String.format("pointHistoryId=%d is not found", pointHistoryId));        }

        return PointHistoryResponse.fromEntity(history);
    }

    @Override
    public PointHistoryResponse createPointHistory(PointHistoryCreateRequest request) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(request.getPointPolicyId()).orElse(null);
        if(Objects.isNull(pointPolicy)){
            throw new PointPolicyNotFoundException(String.format("pointPolicyId=%d is not found", request.getPointPolicyId()));        }

        PointHistory pointHistory = new PointHistory(null,
                request.getTypes(),
                pointPolicy.getAmount(),
                LocalDateTime.now(),
                pointPolicy.getName(),
                request.getMemberId());
        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);
        return PointHistoryResponse.fromEntity(savedHistory);
    }


    @Override
    public Integer getTotalPointByMemberId(Long pointId) {
        return pointHistoryRepository.sumAmount(pointId);
    }


}
