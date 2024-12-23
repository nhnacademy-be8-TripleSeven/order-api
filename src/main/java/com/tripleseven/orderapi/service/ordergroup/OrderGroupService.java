package com.tripleseven.orderapi.service.ordergroup;

import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponse;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupUpdateAddressRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OrderGroupService {

    OrderGroupResponse getOrderGroupById(Long id);

    Page<OrderGroupResponse> getOrderGroupPagesByUserId(Long userId, Pageable pageable);

    OrderGroupResponse createOrderGroup(OrderGroupCreateRequest orderGroupCreateRequest);

    OrderGroupResponse updateAddressOrderGroup(Long id, OrderGroupUpdateAddressRequest orderGroupUpdateAddressRequest);

    void deleteOrderGroup(Long id);

    Page<OrderGroupResponse> getOrderGroupPeriodByUserId(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
