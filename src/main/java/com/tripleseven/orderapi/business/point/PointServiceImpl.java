package com.tripleseven.orderapi.business.point;

import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;
import com.tripleseven.orderapi.entity.defaultpointpolicy.DefaultPointPolicy;
import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.ordergrouppointhistory.OrderGroupPointHistory;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.exception.notfound.OrderGroupNotFoundException;
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
import java.util.Optional;

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

    // 결제 시 포인트 사용 내역 생성
    @Override
    public PointHistoryResponseDTO createPointHistoryForPaymentSpend(Long memberId, int usePoint, Long orderGroupId) {
        PointHistory pointHistory = createPointHistory(
                HistoryTypes.SPEND,
                memberId,
                -usePoint, // 사용 포인트는 음수로 저장
                "포인트 사용"
        );
        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);

        saveOrderGroupHistory(orderGroupId, savedHistory);

        return PointHistoryResponseDTO.fromEntity(savedHistory);
    }

    // 결제 시 포인트 적립 내역 생성
    @Override
    public PointHistoryResponseDTO createPointHistoryForPaymentEarn(Long memberId, int usedMoney, Long orderGroupId) {
        DefaultPointPolicyDTO dto = queryDslDefaultPointPolicyRepository.findDefaultPointPolicyByType(PointPolicyType.DEFAULT_BUY);

        int earnPoint = dto.getAmount() != 0 ?
                dto.getAmount() : dto.getRate().multiply(BigDecimal.valueOf(usedMoney)).intValue();

        PointHistory pointHistory = createPointHistory(
                HistoryTypes.EARN,
                memberId,
                earnPoint, // 정책에 따라 적립 포인트 결정
                dto.getName()
        );
        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);

        saveOrderGroupHistory(orderGroupId, savedHistory);

        return PointHistoryResponseDTO.fromEntity(savedHistory);
    }

    // 회원 가입 포인트 적립
    @Override
    @Transactional
    public PointHistoryResponseDTO createRegisterPointHistory(Long memberId) {
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

        String earnRegisterComment = "회원 가입 적립";
        PointHistory registerPointHistory = pointHistoryRepository.findPointHistoryByComment(earnRegisterComment);

        if (Objects.nonNull(registerPointHistory)) {
            throw new RuntimeException();
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
    private PointHistory createPointHistory(HistoryTypes type, Long memberId, int amount, String comment) {
        return new PointHistory(
                type,
                amount,
                LocalDateTime.now(),
                comment,
                memberId
        );
    }

    private void saveOrderGroupHistory(Long id, PointHistory pointHistory) {
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(id);
        if (optionalOrderGroup.isEmpty()) {
            throw new OrderGroupNotFoundException(id);
        }
        OrderGroup orderGroup = optionalOrderGroup.get();
        OrderGroupPointHistory orderGroupPointHistory = new OrderGroupPointHistory();
        orderGroupPointHistory.ofCreate(
                pointHistory,
                orderGroup
        );
        orderGroupPointHistoryRepository.save(orderGroupPointHistory);
    }

}