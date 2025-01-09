package com.tripleseven.orderapi.dto.ordergrouppointhistory;

import lombok.Value;

@Value
public class OrderGroupPointHistoryRequestDTO {
    Long orderGroupId;
    Long pointHistoryId;
}
