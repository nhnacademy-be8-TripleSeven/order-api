package com.tripleseven.orderapi.service.ordergroup;

import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.order.OrderManageRequestDTO;
import com.tripleseven.orderapi.dto.order.OrderViewDTO;
import com.tripleseven.orderapi.dto.order.OrderViewsRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupUpdateAddressRequestDTO;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.notfound.OrderGroupNotFoundException;
import com.tripleseven.orderapi.exception.notfound.WrappingNotFoundException;
import com.tripleseven.orderapi.repository.orderdetail.querydsl.QueryDslOrderDetailRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Service
public class OrderGroupServiceImpl implements OrderGroupService {

    private final OrderGroupRepository orderGroupRepository;
    private final WrappingRepository wrappingRepository;
    private final QueryDslOrderDetailRepository queryDslOrderDetailRepository;
    private final BookCouponApiClient bookCouponApiClient;

    @Override
    @Transactional(readOnly = true)
    public OrderGroupResponseDTO getOrderGroupById(Long id) {
        Optional<OrderGroup> optionalOrderGroup = orderGroupRepository.findById(id);

        if (optionalOrderGroup.isEmpty()) {
            throw new OrderGroupNotFoundException(id);
        }

        return OrderGroupResponseDTO.fromEntity(optionalOrderGroup.get());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderGroupResponseDTO> getOrderGroupPagesByUserId(Long userId, Pageable pageable) {
//        Page<OrderGroup> orderGroups = orderGroupRepository.findAllByUserId(userId, pageable);
//        return orderGroups.map(OrderGroupResponseDTO::fromEntity);
        return Page.empty();
    }

    @Override
    @Transactional
    public OrderGroupResponseDTO createOrderGroup(Long userId, OrderGroupCreateRequestDTO orderGroupCreateRequestDTO) {
        OrderGroup orderGroup = new OrderGroup();

        Optional<Wrapping> optionalWrapping = wrappingRepository.findById(orderGroupCreateRequestDTO.getWrappingId());

        if (optionalWrapping.isEmpty()) {
            throw new WrappingNotFoundException(orderGroupCreateRequestDTO.getWrappingId());
        }

        orderGroup.ofCreate(
                userId,
                orderGroupCreateRequestDTO.getOrderedName(),
                orderGroupCreateRequestDTO.getRecipientName(),
                orderGroupCreateRequestDTO.getRecipientPhone(),
                orderGroupCreateRequestDTO.getRecipientHomePhone(),
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
            throw new OrderGroupNotFoundException(id);
        }

        OrderGroup orderGroup = optionalOrderGroup.get();
        orderGroup.ofUpdate(orderGroupUpdateAddressRequestDTO.getAddress());

        return OrderGroupResponseDTO.fromEntity(orderGroup);

    }

    @Override
    @Transactional
    public void deleteOrderGroup(Long id) {
        if (!orderGroupRepository.existsById(id)) {
            throw new OrderGroupNotFoundException(id);
        }
        orderGroupRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderViewsRequestDTO> getOrderGroupPeriodByUserId(Long userId, OrderManageRequestDTO manageRequestDTO, Pageable pageable) {
        LocalDate startDateTime = manageRequestDTO.getStartDate();
        LocalDate endDateTime = manageRequestDTO.getEndDate();
        OrderStatus orderStatus = manageRequestDTO.getOrderStatus();

        // 전체 데이터 가져오기
        List<OrderViewDTO> orderViews = queryDslOrderDetailRepository.findAllByPeriod(userId, startDateTime, endDateTime, orderStatus);

        // 그룹별로 데이터를 처리
        Map<Long, OrderViewsRequestDTO> groupedData = new LinkedHashMap<>();

        int count = 0;
        for (OrderViewDTO orderView : orderViews) {
            Long groupId = orderView.getOrderId();

            // 그룹이 이미 존재하면 데이터 갱신
            if (groupedData.containsKey(groupId)) {
                OrderViewsRequestDTO dto = groupedData.get(groupId);

                // 상태값 비교 (더 높은 상태값으로 갱신)
                if (dto.getOrderStatus().ordinal() < orderView.getOrderStatus().ordinal()) {
                    dto.setOrderStatus(orderView.getOrderStatus());
                }

                dto.setOrderContent(String.format("%s 외 %d 종", dto.getOrderContent(), ++count));
                // 가격과 수량 누적
                dto.setPrice(dto.getPrice() + orderView.getPrice() * orderView.getAmount());
                dto.setAmount(dto.getAmount() + orderView.getAmount());
            } else {
                // 새로운 그룹 생성
                count = 0;
                String orderContent = bookCouponApiClient.getBookName(orderView.getBookId());
                groupedData.put(groupId, new OrderViewsRequestDTO(
                        groupId,
                        orderView.getOrderDate(),
                        orderContent,
                        orderView.getPrice() * orderView.getAmount(),
                        orderView.getAmount(),
                        orderView.getOrderStatus(),
                        orderView.getOrdererName(),
                        orderView.getRecipientName()
                ));
            }
        }

        // 정렬된 그룹 데이터를 페이징
        List<OrderViewsRequestDTO> allData = new ArrayList<>(groupedData.values());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allData.size());
        List<OrderViewsRequestDTO> paginatedData = allData.subList(start, end);

        // Step 4: PageImpl로 반환
        return new PageImpl<>(paginatedData, pageable, allData.size());
    }

    @Override
    public List<OrderGroupResponseDTO> getGuestOrderGroups(String phone) {
        return orderGroupRepository.findAllByRecipientPhone(phone)
                .stream()
                .map(OrderGroupResponseDTO::fromEntity)
                .toList();

    }
}
