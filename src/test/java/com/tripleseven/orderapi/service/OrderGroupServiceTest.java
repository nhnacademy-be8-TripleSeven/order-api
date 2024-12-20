package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponse;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequest;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponse;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupUpdateDeliveryInfoRequest;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponse;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.service.deliveryinfo.DeliveryInfoServiceImpl;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupServiceImpl;
import com.tripleseven.orderapi.service.wrapping.WrappingServiceImpl;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderGroupServiceTest {
    @Mock
    private OrderGroupRepository orderGroupRepository;

    @Mock
    private WrappingServiceImpl wrappingService;

    @Mock
    private DeliveryInfoServiceImpl deliveryInfoService;

    @InjectMocks
    private OrderGroupServiceImpl orderGroupService;

    private OrderGroup orderGroup;

    private Wrapping wrapping;

    private DeliveryInfo deliveryInfo;

    @BeforeEach
    void setUp() {
        wrapping = new Wrapping();
        wrapping.ofCreate("Test Wrapping", 100);

        orderGroup = new OrderGroup();
        orderGroup.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", 1000, "Test Address", wrapping);

        deliveryInfo = new DeliveryInfo();
        deliveryInfo.ofCreate("Test DeliveryInfo", 12345678);
    }

    @Test
    void testGetOrderGroupById_Success() {
        when(orderGroupRepository.findById(anyLong())).thenReturn(Optional.of(orderGroup));
        OrderGroupResponse response = orderGroupService.getOrderGroupById(1L);
        assertThrows(LazyInitializationException.class, () -> {
            orderGroupService.getOrderGroupPagesByUserId(1L, PageRequest.ofSize(1)).getSize();
        });

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

        assertThrows(RuntimeException.class, () -> orderGroupService.getOrderGroupById(1L));
        verify(orderGroupRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateOrderGroup_Success() {
        when(wrappingService.getWrappingById(anyLong())).thenReturn(WrappingResponse.fromEntity(wrapping));
        when(orderGroupRepository.save(any())).thenReturn(orderGroup);

        OrderGroupResponse response = orderGroupService.createOrderGroup(
                new OrderGroupCreateRequest(
                        orderGroup.getUserId(),
                        1L,
                        orderGroup.getOrderedName(),
                        orderGroup.getRecipientName(),
                        orderGroup.getRecipientPhone(),
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
        when(wrappingService.getWrappingById(anyLong())).thenReturn(null);
        assertThrows(RuntimeException.class, () -> orderGroupService.createOrderGroup(
                new OrderGroupCreateRequest(
                        orderGroup.getUserId(),
                        1L,
                        orderGroup.getOrderedName(),
                        orderGroup.getRecipientName(),
                        orderGroup.getRecipientPhone(),
                        orderGroup.getDeliveryPrice(),
                        orderGroup.getAddress())));

        verify(orderGroupRepository, times(0)).save(any());
    }

    @Test
    void testUpdateOrderGroup_Success() {
        when(deliveryInfoService.getDeliveryInfoById(anyLong())).thenReturn(DeliveryInfoResponse.fromEntity(deliveryInfo));
        when(orderGroupRepository.findById(anyLong())).thenReturn(Optional.of(orderGroup));

        OrderGroupResponse response = orderGroupService.updateOrderGroup(
                1L,
                new OrderGroupUpdateDeliveryInfoRequest(1L));

        assertNotNull(response);
        assertEquals("Test Ordered", response.getOrderedName());
        assertEquals("Test Recipient", response.getRecipientName());
        assertEquals("01012345678", response.getRecipientPhone());
        assertEquals(1000, response.getDeliveryPrice());

        verify(orderGroupRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateOrderGroup_Fail() {
        when(deliveryInfoService.getDeliveryInfoById(anyLong())).thenReturn(null);
        when(orderGroupRepository.findById(anyLong())).thenReturn(Optional.of(orderGroup));

        assertThrows(RuntimeException.class, () -> orderGroupService.updateOrderGroup(
                1L,
                new OrderGroupUpdateDeliveryInfoRequest(1L)));

        verify(orderGroupRepository, times(1)).findById(anyLong());
        verify(deliveryInfoService, times(1)).getDeliveryInfoById(1L);
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
        assertThrows(RuntimeException.class, () -> orderGroupService.deleteOrderGroup(1L));
        verify(orderGroupRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void testGetOrderGroupPages() {
        Pageable pageable = PageRequest.of(0, 10);
        OrderGroup orderGroup2 = new OrderGroup();
        orderGroup2.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", 1000, "Test Address", wrapping);
        when(orderGroupRepository.findAllByUserId(anyLong(), any())).thenReturn(new PageImpl<>(List.of(orderGroup, orderGroup2), pageable, 2));

        Page<OrderGroupResponse> result = orderGroupService.getOrderGroupPagesByUserId(1L, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(orderGroup.getId(), result.getContent().getFirst().getId());
        assertEquals(orderGroup2.getId(), result.getContent().getLast().getId());
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSize());
        assertEquals(2, result.getTotalElements());

        verify(orderGroupRepository, times(1)).findAllByUserId(anyLong(), any());
    }
}
