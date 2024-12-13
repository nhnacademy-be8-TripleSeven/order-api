package com.example.orderapi.service.impl;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
import com.example.orderapi.entity.PointHistory.HistoryTypes;
import com.example.orderapi.entity.PointHistory.PointHistory;
import com.example.orderapi.entity.PointPolicy.PointPolicy;
import com.example.orderapi.exception.notfound.impl.PointHistoryNotFoundException;
import com.example.orderapi.exception.notfound.impl.PointPolicyNotFoundException;
import com.example.orderapi.repository.PointHistoryRepository;
import com.example.orderapi.repository.PointPolicyRepository;
import com.example.orderapi.service.PointHistoryService;
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
    public Page<PointHistoryResponse> findByMemberId(Long memberId, Pageable pageable) {
        Page<PointHistory> histories = pointHistoryRepository.findAllByMemberId(memberId, pageable);
        if(Objects.isNull(histories)){
            throw new PointHistoryNotFoundException("memberId="+memberId +" 's pointHistory is null");
        }


        return histories.map(PointHistoryResponse::fromEntity);
    }

    @Override
    public Page<PointHistory> findAll(Pageable pageable) {

        return pointHistoryRepository.findAll(pageable);
    }

    @Override
    public void deleteByPointHistoryId(Long pointHistoryId) {
        pointHistoryRepository.deleteById(pointHistoryId);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        pointHistoryRepository.deleteAllByMemberId(memberId);
    }

    @Override
    public PointHistoryResponse findByPointHistoryId(Long pointHistoryId) {
        PointHistory history = pointHistoryRepository.findById(pointHistoryId).orElse(null);
        if(Objects.isNull(history)){
            throw new PointHistoryNotFoundException("pointHistoryId="+pointHistoryId +" is not found");
        }

        return PointHistoryResponse.fromEntity(history);
    }



    @Override
    public PointHistoryResponse save(PointHistoryCreateRequest request) {
       PointHistory history = new PointHistory();
       history.setMemberId(request.getMemberId());
       history.setComment(request.getComment());
       history.setAmount(request.getAmount());
       history.setChanged_at(request.getChanged_at());
       history.setTypes(request.getTypes());

       PointHistory savedHistory = pointHistoryRepository.save(history);
        return PointHistoryResponse.fromEntity(savedHistory);
    }

    @Override
    public PointHistoryResponse save(Long policyId, Long memberId) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(policyId).orElse(null);
        if(Objects.isNull(pointPolicy)){
            throw new PointPolicyNotFoundException("policyId="+policyId +" is not found");
        }
        PointHistory history = new PointHistory();
        history.setMemberId(memberId);
        history.setTypes(HistoryTypes.EARN);
        history.setComment(pointPolicy.getName());
        history.setAmount(pointPolicy.getAmount());
        history.setChanged_at(LocalDateTime.now());

        pointHistoryRepository.save(history);
        return PointHistoryResponse.fromEntity(history);
    }

    @Override
    public int getPoint(Long pointId) {
        return pointHistoryRepository.sumAmount(pointId);
    }


}
