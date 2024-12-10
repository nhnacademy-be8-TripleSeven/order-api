package com.example.orderapi.service;

import com.example.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupResponse;
import com.example.orderapi.dto.ordergroup.OrderGroupUpdateRequest;

public interface OrderGroupService {

    OrderGroupResponse getById(Long id);

    OrderGroupResponse save(OrderGroupCreateRequest orderGroup);

    OrderGroupResponse update(Long id, OrderGroupUpdateRequest orderGroupUpdateRequest);

    void delete(Long id);
}
