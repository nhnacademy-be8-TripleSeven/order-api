package com.tripleseven.orderapi.service.orderdetail;

import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequest;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponse;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailUpdateStatusRequest;
import com.tripleseven.orderapi.entity.orderdetail.Status;

import java.util.List;

public interface OrderDetailService {
    OrderDetailResponse getOrderDetailService(Long id);

    OrderDetailResponse createOrderDetail(OrderDetailCreateRequest orderDetailCreateRequest);

    OrderDetailResponse updateOrderDetailStatus(Long id, OrderDetailUpdateStatusRequest orderDetailUpdateStatusRequest);

    void deleteOrderDetail(Long id);

    List<OrderDetailResponse> getOrderDetailsToList(Long orderGroupId);

    List<OrderDetailResponse> getOrderDetailsForGroupWithStatus(Long orderGroupId, Status status);
}
