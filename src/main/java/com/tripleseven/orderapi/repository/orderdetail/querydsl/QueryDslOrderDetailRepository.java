package com.tripleseven.orderapi.repository.orderdetail.querydsl;

import com.tripleseven.orderapi.dto.order.OrderViewDTO;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;

import java.time.LocalDate;
import java.util.List;

public interface QueryDslOrderDetailRepository {


    List<OrderViewDTO> findAllByPeriodAndUserId(Long userId,
                                                LocalDate startTime,
                                                LocalDate endTime,
                                                OrderStatus orderStatus);

    List<OrderViewDTO> findAllByPeriod(LocalDate startTime,
                                       LocalDate endTime,
                                       OrderStatus orderStatus);

    Long computeNetTotal(Long userId, LocalDate startDate, LocalDate endDate);
}
