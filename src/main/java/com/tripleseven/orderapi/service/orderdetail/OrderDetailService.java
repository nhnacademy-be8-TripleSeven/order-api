package com.tripleseven.orderapi.service.orderdetail;

import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public interface OrderDetailService {
    OrderDetailResponseDTO getOrderDetailService(Long id);

    OrderDetailResponseDTO createOrderDetail(OrderDetailCreateRequestDTO orderDetailCreateRequestDTO);

    List<OrderDetailResponseDTO> updateOrderDetailStatus(List<Long> ids, OrderStatus orderStatus);

    List<OrderDetailResponseDTO> updateAdminOrderDetailStatus(List<Long> ids, OrderStatus orderStatus);

    void deleteOrderDetail(Long id);

    List<OrderDetailResponseDTO> getOrderDetailsToList(Long orderGroupId);

    boolean hasUserPurchasedBook(Long userId, Long bookId);

    Long getNetTotalByPeriod(Long userId, LocalDate startDate, LocalDate endDate);

    @Transactional
    void completeOverdueShipments(Duration duration);
}
