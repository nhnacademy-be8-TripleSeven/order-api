package com.example.orderapi.service.pointservice;

import com.example.orderapi.dto.pointhistory.PointHistoryResponse;
import com.example.orderapi.entity.pointhistory.HistoryTypes;
import com.example.orderapi.entity.pointhistory.PointHistory;
import com.example.orderapi.entity.pointpolicy.PointPolicy;
import com.example.orderapi.exception.notfound.PointPolicyNotFoundException;
import com.example.orderapi.repository.pointhistory.PointHistoryRepository;
import com.example.orderapi.repository.pointpolicy.PointPolicyRepository;
import com.example.orderapi.service.pointpolicy.PointPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService{

    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicyRepository pointPolicyRepository;


    //
    @Override
    public PointHistoryResponse createPointHistoryForPaymentSpend(Long memberId, int usePoint) {
        PointPolicy policy = pointPolicyRepository.findById(3L).orElse(null);//결제 관련 정책이 3번에 저장됨
        if(Objects.isNull(policy)){
            throw new PointPolicyNotFoundException("point policy not found");
        }
        PointHistory pointHistory = new PointHistory(
                HistoryTypes.SPEND,
                usePoint * -1,
                LocalDateTime.now(),
                policy.getName(),
                memberId
        );
        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);
        return PointHistoryResponse.fromEntity(savedHistory);
    }

    @Override
    public PointHistoryResponse createPointHistoryForPaymentEarn(Long memberId, int payAmount, Long pointPolicyId) {
        PointPolicy policy = pointPolicyRepository.findById(pointPolicyId).orElse(null);
        if(Objects.isNull(policy)){
            throw new PointPolicyNotFoundException("point policy not found");
        }
        PointHistory pointHistory = new PointHistory(
                HistoryTypes.EARN,
                policy.getAmount(),
                LocalDateTime.now(),
                policy.getName(),
                memberId
        );

        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);
        return PointHistoryResponse.fromEntity(savedHistory);
    }
}
