package com.tripleseven.orderapi.service.ordergroup;

import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupUpdateAddressRequestDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponseDTO;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import com.tripleseven.orderapi.service.wrapping.WrappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderGroupServiceImpl implements OrderGroupService {

    private final OrderGroupRepository orderGroupRepository;
    private final WrappingRepository wrappingRepository;

    @Override
    @Transactional(readOnly = true)
    public OrderGroupResponseDTO getOrderGroupById(Long id) {
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(id);

        if (optionalOrderGroup.isEmpty()) {
            throw new RuntimeException();
        }

        return OrderGroupResponseDTO.fromEntity(optionalOrderGroup.get());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderGroupResponseDTO> getOrderGroupPagesByUserId(Long userId, Pageable pageable) {
        Page<OrderGroup> orderGroups = orderGroupRepository.findAllByUserId(userId, pageable);
        return orderGroups.map(OrderGroupResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public OrderGroupResponseDTO createOrderGroup(OrderGroupCreateRequestDTO orderGroupCreateRequestDTO) {
        OrderGroup orderGroup = new OrderGroup();

        Optional<Wrapping> optionalWrapping = wrappingRepository.findById(orderGroupCreateRequestDTO.getWrappingId());

        if(optionalWrapping.isEmpty()){
            throw new RuntimeException();
        }

        orderGroup.ofCreate(
                orderGroupCreateRequestDTO.getUserId(),
                orderGroupCreateRequestDTO.getOrderedName(),
                orderGroupCreateRequestDTO.getRecipientName(),
                orderGroupCreateRequestDTO.getRecipientPhone(),
                orderGroupCreateRequestDTO.getDeliveryPrice(),
                orderGroupCreateRequestDTO.getAddress(),
                optionalWrapping.get());

        OrderGroup savedOrderGroup = orderGroupRepository.save(orderGroup);

        return OrderGroupResponseDTO.fromEntity(savedOrderGroup);
    }

    @Override
    @Transactional
    public OrderGroupResponseDTO updateAddressOrderGroup(Long id, OrderGroupUpdateAddressRequestDTO orderGroupUpdateAddressRequestDTO) {
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(id);

        if (optionalOrderGroup.isEmpty()) {
            throw new RuntimeException();
        }

        OrderGroup orderGroup = optionalOrderGroup.get();
        orderGroup.ofUpdate(orderGroupUpdateAddressRequestDTO.getAddress());

        return OrderGroupResponseDTO.fromEntity(orderGroup);

    }

    @Override
    @Transactional
    public void deleteOrderGroup(Long id) {
        if (!orderGroupRepository.existsById(id)) {
            throw new RuntimeException();
        }
        orderGroupRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderGroupResponseDTO> getOrderGroupPeriodByUserId(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        ZonedDateTime startDateTime = startDate.atStartOfDay().atZone(ZoneId.systemDefault());
        ZonedDateTime endDateTime = endDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault());

        Page<OrderGroup> savedOrderGroup = orderGroupRepository.findAllByPeriod(userId, startDateTime, endDateTime, pageable);
        if (savedOrderGroup.getContent().isEmpty()) {
            throw new RuntimeException();
        }

        return savedOrderGroup.map(OrderGroupResponseDTO::fromEntity);
    }

}
