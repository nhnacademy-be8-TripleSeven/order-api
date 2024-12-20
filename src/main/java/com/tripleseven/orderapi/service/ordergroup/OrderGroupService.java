package com.tripleseven.orderapi.service.ordergroup;

import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponse;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupUpdateDeliveryInfoRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderGroupService {

    OrderGroupResponse getOrderGroupById(Long id);

    Page<OrderGroupResponse> getOrderGroupPagesByUserId(Long userId, Pageable pageable);

    OrderGroupResponse updateOrderGroup(Long id, OrderGroupUpdateDeliveryInfoRequest orderGroupUpdateRequest);

    OrderGroupResponse createOrderGroup(OrderGroupCreateRequest orderGroupCreateRequest);

    void deleteOrderGroup(Long id);
}
