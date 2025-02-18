package com.tripleseven.orderapi.service.ordergroup;

import com.tripleseven.orderapi.dto.order.OrderViewsResponseDTO;
import com.tripleseven.orderapi.dto.order.OrderManageRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupUpdateAddressRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderGroupService {

    OrderGroupResponseDTO getOrderGroupById(Long id);

    OrderGroupResponseDTO createOrderGroup(Long userId, OrderGroupCreateRequestDTO orderGroupCreateRequestDTO);

    OrderGroupResponseDTO updateAddressOrderGroup(Long id, OrderGroupUpdateAddressRequestDTO orderGroupUpdateAddressRequestDTO);

    void deleteOrderGroup(Long id);

    Page<OrderViewsResponseDTO> getOrderGroupPeriodByUserId(Long userId, OrderManageRequestDTO orderManageRequestDTO, Pageable pageable);

    Page<OrderViewsResponseDTO> getOrderGroupPeriod(OrderManageRequestDTO orderManageRequestDTO, Pageable pageable);

    List<OrderGroupResponseDTO> getGuestOrderGroups(String phone);
}
