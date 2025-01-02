package com.tripleseven.orderapi.dto.order;

import com.tripleseven.orderapi.entity.orderdetail.Status;
import lombok.Value;

import java.time.LocalDate;

@Value
public class OrderManageRequestDTO {
    LocalDate startDate;
    LocalDate endDate;
    Status status;
}
