package com.tripleseven.orderapi.service.orderdetail;

import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailUpdateStatusRequestDTO;
import com.tripleseven.orderapi.entity.orderdetail.Status;

import java.util.List;

public interface OrderDetailService {
    OrderDetailResponseDTO getOrderDetailService(Long id);

    OrderDetailResponseDTO createOrderDetail(OrderDetailCreateRequestDTO orderDetailCreateRequestDTO);

    OrderDetailResponseDTO updateOrderDetailStatus(Long id, OrderDetailUpdateStatusRequestDTO orderDetailUpdateStatusRequestDTO);

    void deleteOrderDetail(Long id);

    List<OrderDetailResponseDTO> getOrderDetailsToList(Long orderGroupId);

    List<OrderDetailResponseDTO> getOrderDetailsForGroupWithStatus(Long orderGroupId, Status status);

    boolean hasUserPurchasedBook(Long userId, Long bookId);
}
