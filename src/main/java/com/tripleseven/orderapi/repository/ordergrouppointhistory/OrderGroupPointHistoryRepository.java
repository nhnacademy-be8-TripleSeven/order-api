package com.tripleseven.orderapi.repository.ordergrouppointhistory;

import com.tripleseven.orderapi.entity.ordergrouppointhistory.OrderGroupPointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderGroupPointHistoryRepository extends JpaRepository<OrderGroupPointHistory, Long> {
}
