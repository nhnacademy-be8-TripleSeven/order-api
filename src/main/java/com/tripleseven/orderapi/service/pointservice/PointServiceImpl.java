package com.tripleseven.orderapi.service.pointservice;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponse;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.notfound.PointPolicyNotFoundException;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicyRepository pointPolicyRepository;

    // 결제 관련 정책 ID를 상수로 정의
    private static final Long PAYMENT_POLICY_ID = 3L;

    // 결제 시 포인트 사용 내역 생성
    @Override
    public PointHistoryResponse createPointHistoryForPaymentSpend(Long memberId, int usePoint) {
        PointPolicy policy = getPointPolicyById(PAYMENT_POLICY_ID); // 결제 관련 정책 조회
        PointHistory pointHistory = createPointHistory(
                HistoryTypes.SPEND,
                memberId,
                usePoint * -1, // 사용 포인트는 음수로 저장
                policy.getName()
        );
        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);
        return PointHistoryResponse.fromEntity(savedHistory);
    }

    // 결제 시 포인트 적립 내역 생성
    @Override
    public PointHistoryResponse createPointHistoryForPaymentEarn(Long memberId, int payAmount, Long pointPolicyId) {
        PointPolicy policy = getPointPolicyById(pointPolicyId); // 적립 정책 조회
        PointHistory pointHistory = createPointHistory(
                HistoryTypes.EARN,
                memberId,
                policy.getAmount(), // 정책에 따라 적립 포인트 결정
                policy.getName()
        );
        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);
        return PointHistoryResponse.fromEntity(savedHistory);
    }

    // 정책 조회 공통 메서드
    private PointPolicy getPointPolicyById(Long policyId) {
        return pointPolicyRepository.findById(policyId)
                .orElseThrow(() -> new PointPolicyNotFoundException("PointPolicyId=" + policyId + " not found"));
    }

    // PointHistory 생성 공통 메서드
    private PointHistory createPointHistory(HistoryTypes type, Long memberId, int amount, String comment) {
        return new PointHistory(
                type,
                amount,
                LocalDateTime.now(),
                comment,
                memberId
        );
    }
}