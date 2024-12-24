package com.tripleseven.orderapi.service.orderdetail;

import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailUpdateStatusRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponseDTO;
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

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final WrappingService wrappingService;
    private final OrderGroupService orderGroupService;

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDTO getOrderDetailService(Long id) {
        Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(id);
        if (optionalOrderDetail.isEmpty()) {
            throw new RuntimeException();
        }

        return OrderDetailResponseDTO.fromEntity(optionalOrderDetail.get());
    }

    @Override
    @Transactional
    public OrderDetailResponseDTO createOrderDetail(OrderDetailCreateRequestDTO orderDetailCreateRequestDTO) {
        OrderDetail orderDetail = new OrderDetail();

        WrappingResponseDTO wrappingResponseDTO = wrappingService.getWrappingById(orderDetailCreateRequestDTO.getWrappingId());
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate(wrappingResponseDTO.getName(), wrappingResponseDTO.getPrice());

        OrderGroupResponseDTO orderGroupResponseDTO = orderGroupService.getOrderGroupById(orderDetailCreateRequestDTO.getOrderGroupId());
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.ofCreate(
                orderGroupResponseDTO.getUserId(),
                orderGroupResponseDTO.getOrderedName(),
                orderGroupResponseDTO.getRecipientName(),
                orderGroupResponseDTO.getRecipientPhone(),
                orderGroupResponseDTO.getDeliveryPrice(),
                orderGroupResponseDTO.getAddress(),
                wrapping);

        orderDetail.ofCreate(
                orderDetailCreateRequestDTO.getBookId(),
                orderDetailCreateRequestDTO.getAmount(),
                orderDetailCreateRequestDTO.getPrice(),
                wrapping,
                orderGroup);

        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);

        return OrderDetailResponseDTO.fromEntity(savedOrderDetail);
    }

    @Override
    @Transactional
    public OrderDetailResponseDTO updateOrderDetailStatus(Long id, OrderDetailUpdateStatusRequestDTO orderDetailUpdateStatusRequestDTO) {
        Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(id);
        if (optionalOrderDetail.isEmpty()) {
            throw new RuntimeException();
        }
        OrderDetail orderDetail = optionalOrderDetail.get();
        orderDetail.ofUpdateStatus(orderDetailUpdateStatusRequestDTO.getStatus());

        return OrderDetailResponseDTO.fromEntity(orderDetail);
    }

    @Override
    @Transactional
    public void deleteOrderDetail(Long id) {
        if (!orderDetailRepository.existsById(id)) {
            throw new RuntimeException();
        }
        orderDetailRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetailResponseDTO> getOrderDetailsToList(Long orderGroupId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderGroupId(orderGroupId);

        if (orderDetails.isEmpty()) {
            throw new RuntimeException();
        }

        return orderDetails.stream().map(OrderDetailResponseDTO::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetailResponseDTO> getOrderDetailsForGroupWithStatus(Long orderGroupId, Status status) {
        return List.of();
    }
}
