package com.example.orderapi.service.orderdetail;

import com.example.orderapi.dto.orderdetail.OrderDetailCreateRequest;
import com.example.orderapi.dto.orderdetail.OrderDetailResponse;
import com.example.orderapi.dto.orderdetail.OrderDetailUpdateStatusRequest;

import java.util.List;

public interface OrderDetailService {
    OrderDetailResponse getOrderDetailService(Long id);

    OrderDetailResponse createOrderDetail(OrderDetailCreateRequest orderDetailCreateRequest);

    OrderDetailResponse updateOrderDetailStatus(Long id, OrderDetailUpdateStatusRequest orderDetailUpdateStatusRequest);

    void deleteOrderDetail(Long id);

    List<OrderDetailResponse> getOrderDetailsToList(Long orderGroupId);
}
