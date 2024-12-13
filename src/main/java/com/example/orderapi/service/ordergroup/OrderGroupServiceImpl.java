package com.example.orderapi.service.ordergroup;

import com.example.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupResponse;
import com.example.orderapi.dto.ordergroup.OrderGroupUpdateRequest;
import com.example.orderapi.entity.OrderGroup;
import com.example.orderapi.repository.ordergroup.OrderGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        if (optionalOrderGroup.isEmpty()) {
            throw new RuntimeException();
        }

        return OrderGroupResponse.fromEntity(optionalOrderGroup.get());
    }

    @Override
    public Page<OrderGroupResponse> getOrderGroupsByUserId(Long userId, Pageable pageable) {
        Page<OrderGroup> orderGroups = orderGroupRepository.findAllByUserId(userId, pageable);
        return orderGroups.map(OrderGroupResponse::fromEntity);
    }

    @Override
    public OrderGroupResponse update(Long id, OrderGroupUpdateRequest orderGroupUpdateRequest) {
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(id);

        if (optionalOrderGroup.isEmpty()) {
            throw new RuntimeException();
        }
        OrderGroup orderGroup = optionalOrderGroup.get();
        orderGroup.setDeliveryInfoId(orderGroupUpdateRequest.getDeliveryInfoId());

        orderGroupRepository.save(orderGroup);

        return OrderGroupResponse.fromEntity(orderGroup);
    }

    @Override
    public OrderGroupResponse create(OrderGroupCreateRequest request) {
        OrderGroup OrderGroup = new OrderGroup();

        OrderGroup.setUserId(request.getUserId());
        OrderGroup.setWrappingId(request.getWrappingId());
        OrderGroup.setOrderedAt(request.getOrderedAt());
        OrderGroup.setRecipientName(request.getRecipientName());
        OrderGroup.setRecipientPhone(request.getRecipientPhone());
        OrderGroup.setDeliveryPrice(request.getDeliveryPrice());

        orderGroupRepository.save(OrderGroup);

        return OrderGroupResponse.fromEntity(OrderGroup);
    }

    @Override
    public void delete(Long id) {
        orderGroupRepository.deleteById(id);
    }

}
