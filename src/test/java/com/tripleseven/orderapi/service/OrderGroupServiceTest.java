package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupUpdateAddressRequestDTO;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.notfound.OrderGroupNotFoundException;
import com.tripleseven.orderapi.exception.notfound.WrappingNotFoundException;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderGroupServiceTest {
    @Mock
    private OrderGroupRepository orderGroupRepository;

    @Mock
    private WrappingRepository wrappingRepository;

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

        assertThrows(OrderGroupNotFoundException.class, () -> orderGroupService.getOrderGroupById(1L));
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
        assertThrows(WrappingNotFoundException.class, () -> orderGroupService.createOrderGroup(
                orderGroup.getId(),
                new OrderGroupCreateRequestDTO(
                        wrapping.getId(),
                        orderGroup.getOrderedName(),
                        orderGroup.getRecipientName(),
                        orderGroup.getRecipientPhone(),
                        orderGroup.getRecipientHomePhone(),
                        orderGroup.getDeliveryPrice(),
                        orderGroup.getAddress())));

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
        when(orderGroupRepository.findById(anyLong())).thenReturn(null);

        assertThrows(NullPointerException.class, () -> orderGroupService.updateAddressOrderGroup(
                1L,
                new OrderGroupUpdateAddressRequestDTO(null)));

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
        assertThrows(OrderGroupNotFoundException.class, () -> orderGroupService.deleteOrderGroup(1L));
        verify(orderGroupRepository, times(0)).deleteById(anyLong());
    }

}
