package com.tripleseven.orderapi.service.ordergroup;

import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupUpdateAddressRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OrderGroupService {

    OrderGroupResponseDTO getOrderGroupById(Long id);

    Page<OrderGroupResponseDTO> getOrderGroupPagesByUserId(Long userId, Pageable pageable);

    OrderGroupResponseDTO createOrderGroup(OrderGroupCreateRequestDTO orderGroupCreateRequestDTO);

    OrderGroupResponseDTO updateAddressOrderGroup(Long id, OrderGroupUpdateAddressRequestDTO orderGroupUpdateAddressRequestDTO);

    void deleteOrderGroup(Long id);

    Page<OrderGroupResponseDTO> getOrderGroupPeriodByUserId(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
