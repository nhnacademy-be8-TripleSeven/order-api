package com.tripleseven.orderapi.service.ordergroup;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponse;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponse;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupUpdateDeliveryInfoRequest;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponse;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.service.deliveryinfo.DeliveryInfoService;
import com.tripleseven.orderapi.service.wrapping.WrappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderGroupServiceImpl implements OrderGroupService {

    private final OrderGroupRepository orderGroupRepository;
    private final DeliveryInfoService deliveryInfoService;
    private final WrappingService wrappingService;

    @Override
    public OrderGroupResponse getOrderGroupById(Long id) {
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(id);

        if (optionalOrderGroup.isEmpty()) {
            throw new RuntimeException();
        }

        return OrderGroupResponse.fromEntity(optionalOrderGroup.get());
    }

    @Override
    public Page<OrderGroupResponse> getOrderGroupPagesByUserId(Long userId, Pageable pageable) {
        Page<OrderGroup> orderGroups = orderGroupRepository.findAllByUserId(userId, pageable);
        return orderGroups.map(OrderGroupResponse::fromEntity);
    }

    @Override
    public OrderGroupResponse updateOrderGroup(Long id, OrderGroupUpdateDeliveryInfoRequest OrderGroupUpdateDeliveryInfoRequest) {
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(id);

        if (optionalOrderGroup.isEmpty()) {
            throw new RuntimeException();
        }

        OrderGroup orderGroup = optionalOrderGroup.get();

        DeliveryInfoResponse deliveryInfoResponse = deliveryInfoService.getDeliveryInfoById(OrderGroupUpdateDeliveryInfoRequest.getDeliveryInfoId());
        DeliveryInfo deliveryInfo = new DeliveryInfo();

        deliveryInfo.ofCreate(deliveryInfoResponse.getName(), deliveryInfoResponse.getInvoiceNumber());

        orderGroup.ofUpdateDeliveryInfo(deliveryInfo);

        return OrderGroupResponse.fromEntity(orderGroup);
    }

    @Override
    public OrderGroupResponse createOrderGroup(OrderGroupCreateRequest orderGroupCreateRequest) {
        OrderGroup orderGroup = new OrderGroup();

        WrappingResponse wrappingResponse = wrappingService.getWrappingById(orderGroupCreateRequest.getWrappingId());
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate(wrappingResponse.getName(), wrappingResponse.getPrice());

        orderGroup.ofCreate(
                orderGroupCreateRequest.getUserId(),
                orderGroupCreateRequest.getOrderedName(),
                orderGroupCreateRequest.getRecipientName(),
                orderGroupCreateRequest.getRecipientPhone(),
                orderGroupCreateRequest.getDeliveryPrice(),
                orderGroupCreateRequest.getAddress(),
                wrapping);

        OrderGroup savedOrderGroup = orderGroupRepository.save(orderGroup);

        return OrderGroupResponse.fromEntity(savedOrderGroup);
    }

    @Override
    public void deleteOrderGroup(Long id) {
        if (!orderGroupRepository.existsById(id)) {
            throw new RuntimeException();
        }
        orderGroupRepository.deleteById(id);
    }

}
