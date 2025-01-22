package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.order.OrderManageRequestDTO;
import com.tripleseven.orderapi.dto.order.OrderViewDTO;
import com.tripleseven.orderapi.dto.order.OrderViewsResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupUpdateAddressRequestDTO;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.repository.orderdetail.querydsl.QueryDslOrderDetailRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderGroupServiceTest {
    @Mock
    private OrderGroupRepository orderGroupRepository;

    @Mock
    private WrappingRepository wrappingRepository;

    @Mock
    private QueryDslOrderDetailRepository queryDslOrderDetailRepository;

    @Mock
    private BookCouponApiClient bookCouponApiClient;

    @InjectMocks
    private OrderGroupServiceImpl orderGroupService;

    private OrderGroup orderGroup;

    private Wrapping wrapping;

    @BeforeEach
    void setUp() {
        wrapping = new Wrapping();
        ReflectionTestUtils.setField(wrapping, "id", 1L);
        wrapping.ofCreate("Test Wrapping", 100);

        orderGroup = new OrderGroup();
        ReflectionTestUtils.setField(orderGroup, "id", 1L);
        orderGroup.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", "01012345678", 1000, "Test Address", wrapping);
    }

    @Test
    void testGetOrderGroupById_Success() {
        when(orderGroupRepository.findById(anyLong())).thenReturn(Optional.of(orderGroup));
        OrderGroupResponseDTO response = orderGroupService.getOrderGroupById(1L);

        assertNotNull(response);
        assertEquals("Test Ordered", response.getOrderedName());
        assertEquals("Test Recipient", response.getRecipientName());
        assertEquals("01012345678", response.getRecipientPhone());
        assertEquals(1000, response.getDeliveryPrice());
        assertEquals("Test Address", response.getAddress());
    }

    @Test
    void testGetOrderGroupById_Fail() {
        when(orderGroupRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> orderGroupService.getOrderGroupById(1L));
        verify(orderGroupRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateOrderGroup_Success() {
        when(wrappingRepository.findById(anyLong())).thenReturn(Optional.of(wrapping));
        when(orderGroupRepository.save(any())).thenReturn(orderGroup);

        OrderGroupResponseDTO response = orderGroupService.createOrderGroup(
                orderGroup.getId(),
                new OrderGroupCreateRequestDTO(
                        wrapping.getId(),
                        orderGroup.getOrderedName(),
                        orderGroup.getRecipientName(),
                        orderGroup.getRecipientPhone(),
                        orderGroup.getRecipientHomePhone(),
                        orderGroup.getDeliveryPrice(),
                        orderGroup.getAddress()));

        assertNotNull(response);
        assertEquals("Test Ordered", response.getOrderedName());
        assertEquals("Test Recipient", response.getRecipientName());
        assertEquals("01012345678", response.getRecipientPhone());
        assertEquals(1000, response.getDeliveryPrice());

        verify(orderGroupRepository, times(1)).save(any());
    }

    @Test
    void testCreateOrderGroup_Fail() {
        Long orderGroupId = orderGroup.getId();
        OrderGroupCreateRequestDTO requestDTO = new OrderGroupCreateRequestDTO(
                wrapping.getId(),
                orderGroup.getOrderedName(),
                orderGroup.getRecipientName(),
                orderGroup.getRecipientPhone(),
                orderGroup.getRecipientHomePhone(),
                orderGroup.getDeliveryPrice(),
                orderGroup.getAddress()
        );

        CustomException exception = assertThrows(CustomException.class,
                () -> orderGroupService.createOrderGroup(orderGroupId, requestDTO));

        assertNotNull(exception);
        verify(orderGroupRepository, times(0)).save(any());
    }

    @Test
    void testUpdateAddressOrderGroup_Success() {
        when(orderGroupRepository.findById(anyLong())).thenReturn(Optional.of(orderGroup));

        OrderGroupResponseDTO response = orderGroupService.updateAddressOrderGroup(
                1L,
                new OrderGroupUpdateAddressRequestDTO("Test Address"));

        assertNotNull(response);
        assertEquals("Test Ordered", response.getOrderedName());
        assertEquals("Test Recipient", response.getRecipientName());
        assertEquals("01012345678", response.getRecipientPhone());
        assertEquals(1000, response.getDeliveryPrice());

        verify(orderGroupRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateAddressOrderGroup_Fail() {
        OrderGroupUpdateAddressRequestDTO requestDTO = new OrderGroupUpdateAddressRequestDTO(null);

        when(orderGroupRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> orderGroupService.updateAddressOrderGroup(1L, requestDTO));

        assertNotNull(exception);
        verify(orderGroupRepository, times(1)).findById(anyLong());
    }

    @Test
    void testDeleteOrderGroup_Success() {
        when(orderGroupRepository.existsById(anyLong())).thenReturn(true);
        orderGroupService.deleteOrderGroup(1L);

        verify(orderGroupRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testDeleteOrderGroup_Fail() {
        when(orderGroupRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(CustomException.class, () -> orderGroupService.deleteOrderGroup(1L));
        verify(orderGroupRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void testGetOrderGroupPeriodByUserId_Success() {
        List<OrderViewDTO> orderViewList = List.of(
                new OrderViewDTO(1L, LocalDate.now(), 101L, 15000, 2, OrderStatus.PAYMENT_COMPLETED, "John Doe", "Jane Doe"),
                new OrderViewDTO(1L, LocalDate.now(), 102L, 20000, 1, OrderStatus.SHIPPING, "Alice", "Bob")
        );

        OrderManageRequestDTO dto = new OrderManageRequestDTO(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                OrderStatus.PAYMENT_COMPLETED);

        when(bookCouponApiClient.getBookName(anyLong())).thenReturn("Book A");
        when(queryDslOrderDetailRepository.findAllByPeriodAndUserId(
                any(Long.class),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(OrderStatus.PAYMENT_COMPLETED)))
                .thenReturn(orderViewList);


        Page<OrderViewsResponseDTO> result = orderGroupService.getOrderGroupPeriodByUserId(1L, dto, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        OrderViewsResponseDTO firstResponse = result.getContent().get(0);
        assertEquals(1L, firstResponse.getOrderId());
        assertEquals("Book A 외 1 종", firstResponse.getOrderContent());
        assertEquals(50000, firstResponse.getPrice());
        assertEquals(OrderStatus.PAYMENT_COMPLETED, firstResponse.getOrderStatus());
        assertEquals("John Doe", firstResponse.getOrdererName());
        assertEquals("Jane Doe", firstResponse.getRecipientName());
    }

    @Test
    void testGetOrderGroupPeriod_Success() {
        List<OrderViewDTO> orderViewList = List.of(
                new OrderViewDTO(1L, LocalDate.now(), 101L, 15000, 2, OrderStatus.PAYMENT_COMPLETED, "John Doe", "Jane Doe"),
                new OrderViewDTO(2L, LocalDate.now(), 102L, 20000, 1, OrderStatus.PAYMENT_COMPLETED, "Alice", "Bob")
        );

        OrderManageRequestDTO dto = new OrderManageRequestDTO(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                OrderStatus.PAYMENT_COMPLETED
        );

        when(queryDslOrderDetailRepository.findAllByPeriod(
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getOrderStatus()
        )).thenReturn(orderViewList);

        Page<OrderViewsResponseDTO> result = orderGroupService.getOrderGroupPeriod(dto, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        OrderViewsResponseDTO firstResponse = result.getContent().get(0);
        assertEquals(1L, firstResponse.getOrderId());
        assertEquals("John Doe", firstResponse.getOrdererName());
        assertEquals("Jane Doe", firstResponse.getRecipientName());
    }

    @Test
    void testGetGuestOrderGroups_Success() {
        OrderGroup orderGroup2 = new OrderGroup();
        ReflectionTestUtils.setField(orderGroup2, "id", 2L);
        orderGroup2.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", "01012345678", 1000, "Test Address", wrapping);

        List<OrderGroup> orderGroups = List.of(
                orderGroup,
                orderGroup2
        );

        when(orderGroupRepository.findAllByRecipientPhone("01012345678"))
                .thenReturn(orderGroups);

        List<OrderGroupResponseDTO> result = orderGroupService.getGuestOrderGroups("01012345678");

        assertNotNull(result);
        assertEquals(2, result.size());

        OrderGroupResponseDTO firstGroup = result.get(0);
        assertEquals("Test Recipient", firstGroup.getRecipientName());
        assertEquals("01012345678", firstGroup.getRecipientPhone());
    }
}
