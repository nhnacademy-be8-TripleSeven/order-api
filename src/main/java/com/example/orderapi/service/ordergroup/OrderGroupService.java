package com.example.orderapi.service.ordergroup;

import com.example.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupResponse;
import com.example.orderapi.dto.ordergroup.OrderGroupUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderGroupService {

    OrderGroupResponse getById(Long id);

    Page<OrderGroupResponse> getOrderGroupsByUserId(Long userId, Pageable pageable);

    OrderGroupResponse update(Long id, OrderGroupUpdateRequest orderGroupUpdateRequest);

    OrderGroupResponse create(OrderGroupCreateRequest orderGroupCreateRequest);

    void delete(Long id);
}
