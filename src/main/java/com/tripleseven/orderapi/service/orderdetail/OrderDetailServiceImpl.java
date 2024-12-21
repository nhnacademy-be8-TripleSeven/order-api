package com.tripleseven.orderapi.service.orderdetail;

import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequest;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponse;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailUpdateStatusRequest;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponse;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponse;
import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.Status;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.orderdetail.OrderDetailRepository;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.wrapping.WrappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final WrappingService wrappingService;
    private final OrderGroupService orderGroupService;

    @Override
    public OrderDetailResponse getOrderDetailService(Long id) {
        Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(id);
        if (optionalOrderDetail.isEmpty()) {
            throw new RuntimeException();
        }

        return OrderDetailResponse.fromEntity(optionalOrderDetail.get());
    }

    @Override
    public OrderDetailResponse createOrderDetail(OrderDetailCreateRequest orderDetailCreateRequest) {
        OrderDetail orderDetail = new OrderDetail();

        WrappingResponse wrappingResponse = wrappingService.getWrappingById(orderDetailCreateRequest.getWrappingId());
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate(wrappingResponse.getName(), wrappingResponse.getPrice());

        OrderGroupResponse orderGroupResponse = orderGroupService.getOrderGroupById(orderDetailCreateRequest.getOrderGroupId());
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.ofCreate(
                orderGroupResponse.getUserId(),
                orderGroupResponse.getOrderedName(),
                orderGroupResponse.getRecipientName(),
                orderGroupResponse.getRecipientPhone(),
                orderGroupResponse.getDeliveryPrice(),
                orderGroupResponse.getAddress(),
                wrapping);

        orderDetail.ofCreate(
                orderDetailCreateRequest.getBookId(),
                orderDetailCreateRequest.getAmount(),
                orderDetailCreateRequest.getPrice(),
                wrapping,
                orderGroup);

        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);

        return OrderDetailResponse.fromEntity(savedOrderDetail);
    }

    @Override
    public OrderDetailResponse updateOrderDetailStatus(Long id, OrderDetailUpdateStatusRequest orderDetailUpdateStatusRequest) {
        Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(id);
        if (optionalOrderDetail.isEmpty()) {
            throw new RuntimeException();
        }
        OrderDetail orderDetail = optionalOrderDetail.get();
        orderDetail.ofUpdateStatus(orderDetailUpdateStatusRequest.getStatus());

        return OrderDetailResponse.fromEntity(orderDetail);
    }

    @Override
    public void deleteOrderDetail(Long id) {
        if (!orderDetailRepository.existsById(id)) {
            throw new RuntimeException();
        }
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetailResponse> getOrderDetailsToList(Long orderGroupId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderGroupId(orderGroupId);

        if (orderDetails.isEmpty()) {
            throw new RuntimeException();
        }

        return orderDetails.stream().map(OrderDetailResponse::fromEntity).toList();
    }

    @Override
    public List<OrderDetailResponse> getOrderDetailsForGroupWithStatus(Long orderGroupId, Status status) {
        return List.of();
    }
}
