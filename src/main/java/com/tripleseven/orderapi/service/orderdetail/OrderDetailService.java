package com.tripleseven.orderapi.service.orderdetail;

import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;

import java.util.List;

public interface OrderDetailService {
    OrderDetailResponseDTO getOrderDetailService(Long id);

    OrderDetailResponseDTO createOrderDetail(OrderDetailCreateRequestDTO orderDetailCreateRequestDTO);

    List<OrderDetailResponseDTO> updateOrderDetailStatus(List<Long> ids, OrderStatus orderStatus);

    void deleteOrderDetail(Long id);

    List<OrderDetailResponseDTO> getOrderDetailsToList(Long orderGroupId);

    List<OrderDetailResponseDTO> getOrderDetailsForGroupWithStatus(Long orderGroupId, OrderStatus orderStatus);

    boolean hasUserPurchasedBook(Long userId, Long bookId);
}
