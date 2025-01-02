package com.tripleseven.orderapi.repository.orderdetail.querydsl;

import com.tripleseven.orderapi.dto.order.OrderViewDTO;
import com.tripleseven.orderapi.dto.order.OrderViewsRequestDTO;
import com.tripleseven.orderapi.entity.orderdetail.Status;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface QueryDslOrderDetailRepository {


    List<OrderViewDTO> findAllByPeriod(@Param("userId") Long userId,
                                       @Param("startTime") LocalDate startTime,
                                       @Param("endTime") LocalDate endTime,
                                       @Param("status") Status status);
}
