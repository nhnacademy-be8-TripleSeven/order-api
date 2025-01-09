package com.tripleseven.orderapi.repository.ordergrouppointhistory.querydsl;

import com.tripleseven.orderapi.dto.pointhistory.UserPointHistoryDTO;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;


public interface QueryDslOrderGroupPointHistoryRepository {

    Integer findTotalAmountByOrderGroupId(Long orderGroupId, HistoryTypes historyTypes);

    Page<UserPointHistoryDTO> findUserPointHistories(Long memberId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
