package com.tripleseven.orderapi.business.point;

import com.tripleseven.orderapi.client.MemberApiClient;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;
import com.tripleseven.orderapi.entity.defaultpointpolicy.DefaultPointPolicy;
import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.ordergrouppointhistory.OrderGroupPointHistory;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.defaultpointpolicy.DefaultPointPolicyRepository;
import com.tripleseven.orderapi.repository.defaultpointpolicy.querydsl.QueryDslDefaultPointPolicyRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.ordergrouppointhistory.OrderGroupPointHistoryRepository;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final OrderGroupRepository orderGroupRepository;
    private final QueryDslDefaultPointPolicyRepository queryDslDefaultPointPolicyRepository;
    private final OrderGroupPointHistoryRepository orderGroupPointHistoryRepository;
    private final PointPolicyRepository pointPolicyRepository;
    private final DefaultPointPolicyRepository defaultPointPolicyRepository;

    private final MemberApiClient memberApiClient;

    // 결제 시 포인트 사용 내역 생성
    @Override
    public PointHistoryResponseDTO createPointHistoryForPaymentSpend(Long memberId, long usePoint, Long orderGroupId) {
        PointHistory pointHistory = createPointHistory(
                HistoryTypes.SPEND,
                memberId,
                -usePoint, // 사용 포인트는 음수로 저장
                "포인트 사용"
        );
        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);

        OrderGroup orderGroup = orderGroupRepository.findById(orderGroupId)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

        OrderGroupPointHistory orderGroupPointHistory = new OrderGroupPointHistory();
        orderGroupPointHistory.ofCreate(
                savedHistory,
                orderGroup
        );

        orderGroupPointHistoryRepository.save(orderGroupPointHistory);

        return PointHistoryResponseDTO.fromEntity(savedHistory);
    }

    // 결제 시 포인트 적립 내역 생성 (기본 구매 적립 + 등급별 적립)
    @Override
    public PointHistoryResponseDTO createPointHistoryForPaymentEarn(Long memberId, long usedMoney, Long orderGroupId) {
        DefaultPointPolicyDTO dto = queryDslDefaultPointPolicyRepository.findDefaultPointPolicyByType(PointPolicyType.DEFAULT_BUY);

        if (Objects.isNull(dto)) {
            PointPolicy pointPolicy = new PointPolicy();
            pointPolicy.ofCreate("기본 구매 적립", 0, BigDecimal.ONE);
            PointPolicy savedPointPolicy = pointPolicyRepository.save(pointPolicy);

            DefaultPointPolicy defaultPointPolicy = new DefaultPointPolicy();
            defaultPointPolicy.ofCreate(PointPolicyType.DEFAULT_BUY, savedPointPolicy);
            DefaultPointPolicy savedDefaultPointPolicy = defaultPointPolicyRepository.save(defaultPointPolicy);

            dto = new DefaultPointPolicyDTO(
                    savedDefaultPointPolicy.getId(),
                    savedDefaultPointPolicy.getPointPolicyType(),
                    savedPointPolicy
            );
        }

        int earnPoint = dto.getAmount() != 0 ?
                dto.getAmount() : dto.getRate().multiply(BigDecimal.valueOf(usedMoney)).intValue();

        PointHistory defaultPointHistory = createPointHistory(
                HistoryTypes.EARN,
                memberId,
                earnPoint, // 정책에 따라 적립 포인트 결정
                dto.getName()
        );

        long gradePoint = memberApiClient.getGradePoint(memberId, usedMoney);

        PointHistory graderPointHistory = createPointHistory(
                HistoryTypes.EARN,
                memberId,
                gradePoint,
                "등급 혜택"
        );

        PointHistory savedGradeHistory = pointHistoryRepository.save(graderPointHistory);
        PointHistory savedPointHistory = pointHistoryRepository.save(defaultPointHistory);


        OrderGroup orderGroup = orderGroupRepository.findById(orderGroupId)
                .orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_FOUND));

        OrderGroupPointHistory orderGroupGradePointHistory = new OrderGroupPointHistory();
        orderGroupGradePointHistory.ofCreate(
                savedGradeHistory,
                orderGroup
        );

        OrderGroupPointHistory orderGroupPointHistory = new OrderGroupPointHistory();
        orderGroupPointHistory.ofCreate(
                savedPointHistory,
                orderGroup
        );


        orderGroupPointHistoryRepository.save(orderGroupPointHistory);
        orderGroupPointHistoryRepository.save(orderGroupGradePointHistory);

        return PointHistoryResponseDTO.fromEntity(savedPointHistory);
    }

    // 회원 가입 포인트 적립
    @Override
    @Transactional
    public PointHistoryResponseDTO createRegisterPointHistory(Long memberId) {

        String earnRegisterComment = "회원 가입 적립";

        // 회원가입 적립 내력에 있나 체크
        pointHistoryRepository.findPointHistoryByComment(memberId, earnRegisterComment)
                .ifPresent(history -> {
                    throw new CustomException(ErrorCode.ALREADY_EXIST_CONFLICT);
                });

        DefaultPointPolicyDTO dto = queryDslDefaultPointPolicyRepository.findDefaultPointPolicyByType(PointPolicyType.REGISTER);

        if (Objects.isNull(dto)) {
            PointPolicy pointPolicy = new PointPolicy();
            pointPolicy.ofCreate("회원 가입 정책", 5000, BigDecimal.ZERO);

            PointPolicy savedPointPolicy = pointPolicyRepository.save(pointPolicy);

            DefaultPointPolicy defaultPointPolicy = new DefaultPointPolicy();
            defaultPointPolicy.ofCreate(PointPolicyType.REGISTER, savedPointPolicy);

            DefaultPointPolicy savedDefault = defaultPointPolicyRepository.save(defaultPointPolicy);
            dto = new DefaultPointPolicyDTO(
                    savedDefault.getId(),
                    savedDefault.getPointPolicyType(),
                    savedDefault.getPointPolicy()
            );
        }

        PointHistory pointHistory = createPointHistory(
                HistoryTypes.EARN,
                memberId,
                dto.getAmount(),
                earnRegisterComment
        );

        PointHistory savedPointHistory = pointHistoryRepository.save(pointHistory);

        return PointHistoryResponseDTO.fromEntity(savedPointHistory);
    }

    // PointHistory 생성 공통 메서드
    private PointHistory createPointHistory(HistoryTypes type, Long memberId, long amount, String comment) {
        return new PointHistory(
                type,
                amount,
                LocalDateTime.now(),
                comment,
                memberId
        );
    }

}