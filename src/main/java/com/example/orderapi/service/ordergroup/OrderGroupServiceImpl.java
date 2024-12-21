package com.example.orderapi.service.ordergroup;

import com.example.orderapi.dto.deliveryinfo.DeliveryInfoResponse;
import com.example.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.example.orderapi.dto.ordergroup.OrderGroupResponse;
import com.example.orderapi.dto.ordergroup.OrderGroupUpdateDeliveryInfoRequest;
import com.example.orderapi.dto.wrapping.WrappingResponse;
import com.example.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.example.orderapi.entity.ordergroup.OrderGroup;
import com.example.orderapi.entity.wrapping.Wrapping;
import com.example.orderapi.repository.ordergroup.OrderGroupRepository;
import com.example.orderapi.service.deliveryinfo.DeliveryInfoService;
import com.example.orderapi.service.wrapping.WrappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    @Override
    public Page<OrderGroupResponse> getOrderGroupPeriodByUserId(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        ZonedDateTime startDateTime = startDate.atStartOfDay().atZone(ZoneId.systemDefault());
        ZonedDateTime endDateTime = endDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault());

        Page<OrderGroup> savedOrderGroup = orderGroupRepository.findAllByPeriod(userId, startDateTime, endDateTime, pageable);
        if(savedOrderGroup.getContent().isEmpty()) {
            throw new RuntimeException();
        }

        return savedOrderGroup.map(OrderGroupResponse::fromEntity);
    }

}
