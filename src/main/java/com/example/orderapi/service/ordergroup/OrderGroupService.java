package com.example.orderapi.service.ordergroup;

import com.example.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupResponse;
import com.example.orderapi.dto.ordergroup.OrderGroupUpdateDeliveryInfoRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderGroupService {

    OrderGroupResponse getOrderGroupById(Long id);

    Page<OrderGroupResponse> getOrderGroupPagesByUserId(Long userId, Pageable pageable);

    OrderGroupResponse updateOrderGroup(Long id, OrderGroupUpdateDeliveryInfoRequest orderGroupUpdateRequest);

    OrderGroupResponse createOrderGroup(OrderGroupCreateRequest orderGroupCreateRequest);

    void deleteOrderGroup(Long id);
}
