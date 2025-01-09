package com.tripleseven.orderapi.service.ordergrouppointhistory;

import com.tripleseven.orderapi.dto.ordergrouppointhistory.OrderGroupPointHistoryRequestDTO;
import com.tripleseven.orderapi.dto.ordergrouppointhistory.OrderGroupPointHistoryResponseDTO;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.ordergrouppointhistory.OrderGroupPointHistory;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.exception.notfound.OrderGroupNotFoundException;
import com.tripleseven.orderapi.exception.notfound.PointHistoryNotFoundException;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.ordergrouppointhistory.OrderGroupPointHistoryRepository;
import com.tripleseven.orderapi.repository.ordergrouppointhistory.querydsl.QueryDslOrderGroupPointHistoryRepository;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderGroupPointHistoryServiceImpl implements OrderGroupPointHistoryService {

    private final OrderGroupPointHistoryRepository orderGroupPointHistoryRepository;
    private final QueryDslOrderGroupPointHistoryRepository queryDslOrderGroupPointHistoryRepository;
    private final OrderGroupRepository orderGroupRepository;
    private final PointHistoryRepository pointHistoryRepository;

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
    public OrderGroupPointHistoryResponseDTO createOrderGroupPointHistory(OrderGroupPointHistoryRequestDTO request) {
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(request.getOrderGroupId());
        if (optionalOrderGroup.isEmpty()) {
            throw new OrderGroupNotFoundException(request.getOrderGroupId());
        }

        Optional<PointHistory> optionalPointHistory = pointHistoryRepository.findById(request.getPointHistoryId());
        if (optionalPointHistory.isEmpty()) {
            throw new PointHistoryNotFoundException("not fount id: " + request.getPointHistoryId());
        }

        OrderGroup orderGroup = optionalOrderGroup.get();
        PointHistory pointHistory = optionalPointHistory.get();

        OrderGroupPointHistory orderGroupPointHistory = new OrderGroupPointHistory();
        orderGroupPointHistory.ofCreate(pointHistory, orderGroup);
        OrderGroupPointHistory savedOrderGroupPointHistory = orderGroupPointHistoryRepository.save(orderGroupPointHistory);

        return OrderGroupPointHistoryResponseDTO.fromEntity(savedOrderGroupPointHistory);
    }
}
