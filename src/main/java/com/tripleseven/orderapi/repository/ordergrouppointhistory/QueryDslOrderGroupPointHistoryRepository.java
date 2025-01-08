package com.tripleseven.orderapi.repository.ordergrouppointhistory;

import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;

public interface QueryDslOrderGroupPointHistoryRepository {

    Integer findTotalAmountByOrderGroupId(Long orderGroupId, HistoryTypes historyTypes);
}
