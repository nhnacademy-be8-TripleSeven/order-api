package com.tripleseven.orderapi.dto.ordergrouppointhistory;

import com.tripleseven.orderapi.entity.ordergrouppointhistory.OrderGroupPointHistory;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderGroupPointHistoryResponseDTO {
    Long id;
    Long pointHistoryId;
    Long orderGroupId;

    @Builder
    private OrderGroupPointHistoryResponseDTO(Long id, Long pointHistoryId, Long orderGroupId) {
        this.id = id;
        this.pointHistoryId = pointHistoryId;
        this.orderGroupId = orderGroupId;
    }

    public static OrderGroupPointHistoryResponseDTO fromEntity(OrderGroupPointHistory orderGroupPointHistory) {
        return OrderGroupPointHistoryResponseDTO.builder()
                .id(orderGroupPointHistory.getId())
                .pointHistoryId(orderGroupPointHistory.getPointHistory().getId())
                .orderGroupId(orderGroupPointHistory.getOrderGroup().getId())
                .build();
    }
}
