package com.example.orderapi.service.impl;

import com.example.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupResponse;
import com.example.orderapi.dto.ordergroup.OrderGroupUpdateRequest;
import com.example.orderapi.entity.OrderGroup;
import com.example.orderapi.repository.OrderGroupRepository;
import com.example.orderapi.service.OrderGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderGroupServiceImpl implements OrderGroupService {
    private final OrderGroupRepository orderGroupRepository;

    @Override
    public OrderGroupResponse getById(Long id) {
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(id);

        if(optionalOrderGroup.isEmpty()){
            throw new RuntimeException();
        }

        return OrderGroupResponse.fromEntity(optionalOrderGroup.get());
    }

    @Override
    public OrderGroupResponse save(OrderGroupCreateRequest request) {
        OrderGroup orderGroup = new OrderGroup();

        orderGroup.setUserId(request.getUserId());
        orderGroup.setWrappingId(request.getWrappingId());
        orderGroup.setOrderedAt(request.getOrderedAt());
        orderGroup.setRecipientName(request.getRecipientName());
        orderGroup.setRecipientPhone(request.getRecipientPhone());
        orderGroup.setDeliveryPrice(request.getDeliveryPrice());

        return OrderGroupResponse.fromEntity(orderGroup);
    }

    @Override
    public OrderGroupResponse update(Long id, OrderGroupUpdateRequest orderGroupUpdateRequest) {
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(id);

        if(optionalOrderGroup.isEmpty()){
            throw new RuntimeException();
        }

        OrderGroup orderGroup = optionalOrderGroup.get();

        orderGroup.setWrappingId(orderGroup.getWrappingId());
        orderGroup.setRecipientName(orderGroup.getRecipientName());
        orderGroup.setRecipientPhone(orderGroup.getRecipientPhone());

        return OrderGroupResponse.fromEntity(orderGroup);
    }

    @Override
    public void delete(Long id) {
        orderGroupRepository.deleteById(id);
    }
}
