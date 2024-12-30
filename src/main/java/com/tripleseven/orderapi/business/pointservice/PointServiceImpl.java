package com.tripleseven.orderapi.business.pointservice;

import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.notfound.PointPolicyNotFoundException;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicyRepository pointPolicyRepository;

    private static final Long BASIC_EARN_POINT_POLICY_ID = 1L;
    private static final Long REGISTER_POINT_POLICY_ID = 2L;
    private static final Long REVIEW_POINT_POLICY_ID = 3L;
    private static final Long REVIEW_AND_PHOTO_POINT_POLICY_ID = 4L;

    // 결제 관련 정책 ID를 상수로 정의
//    private static final Long PAYMENT_POLICY_ID = 3L;

    // 결제 시 포인트 사용 내역 생성
    @Override
    public PointHistoryResponseDTO createPointHistoryForPaymentSpend(Long memberId, int usePoint) {
        PointHistory pointHistory = createPointHistory(
                HistoryTypes.SPEND,
                memberId,
                -usePoint, // 사용 포인트는 음수로 저장
                "포인트 사용"
        );
        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);
        return PointHistoryResponseDTO.fromEntity(savedHistory);
    }

    // 결제 시 포인트 적립 내역 생성
    @Override
    public PointHistoryResponseDTO createPointHistoryForPaymentEarn(Long memberId, int usedMoney) {
        PointPolicy policy = getPointPolicyById(BASIC_EARN_POINT_POLICY_ID); // 적립 정책 조회

        int earnPoint = policy.getRate().multiply(BigDecimal.valueOf(usedMoney)).intValue();

        PointHistory pointHistory = createPointHistory(
                HistoryTypes.EARN,
                memberId,
                earnPoint, // 정책에 따라 적립 포인트 결정
                policy.getName()
        );
        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);
        return PointHistoryResponseDTO.fromEntity(savedHistory);
    }

    @Override
    public PointHistoryResponseDTO createPointHistoryByAmount(Long memberId, Long policyId) {
        PointPolicy policy = getPointPolicyById(policyId); // 적립 정책 조회


        PointHistory pointHistory = createPointHistory(
                HistoryTypes.EARN,
                memberId,
                policy.getAmount(), // 정책에 따라 적립 포인트 결정
                policy.getName()
        );
        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);
        return PointHistoryResponseDTO.fromEntity(savedHistory);
    }

    @Override
    public PointHistoryResponseDTO createPointHistoryByRate(Long memberId, int usedMoney, Long policyId) {
        PointPolicy policy = getPointPolicyById(policyId); // 적립 정책 조회

        int earnPoint = policy.getRate().multiply(BigDecimal.valueOf(usedMoney)).intValue();

        PointHistory pointHistory = createPointHistory(
                HistoryTypes.EARN,
                memberId,
                earnPoint, // 정책에 따라 적립 포인트 결정
                policy.getName()
        );
        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);
        return PointHistoryResponseDTO.fromEntity(savedHistory);
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