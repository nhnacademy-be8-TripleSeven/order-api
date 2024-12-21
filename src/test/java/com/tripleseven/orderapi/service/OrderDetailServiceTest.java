package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequest;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponse;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailUpdateStatusRequest;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponse;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponse;
import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.Status;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.orderdetail.OrderDetailRepository;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailServiceImpl;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.wrapping.WrappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderDetailServiceTest {
    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private WrappingService wrappingService;

    @Mock
    private OrderGroupService orderGroupService;

    @InjectMocks
    private OrderDetailServiceImpl orderDetailService;

    private Wrapping wrapping;

    private OrderGroup orderGroup;

    private OrderDetail orderDetail;

    @BeforeEach
    void setUp() {
        wrapping = new Wrapping();
        wrapping.ofCreate("Test Wrapping", 100);

        orderGroup = new OrderGroup();
        orderGroup.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", 1000, wrapping);

        orderDetail = new OrderDetail();
        orderDetail.ofCreate(1L, 3, 10000, wrapping, orderGroup);
    }

    @Test
    void testGetOrderDetailById_Success() {
        when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.of(orderDetail));

        OrderDetailResponse response = orderDetailService.getOrderDetailService(1L);

        assertNotNull(response);
        assertEquals(1L, response.getBookId());
        assertEquals(3, response.getAmount());
        assertEquals(10000, response.getPrice());

        verify(orderDetailRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderDetailById_Fail() {
        when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderDetailService.getOrderDetailService(1L));
        verify(orderDetailRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateOrderDetail_Success() {
        when(wrappingService.getWrappingById(anyLong())).thenReturn(WrappingResponse.fromEntity(wrapping));
        when(orderGroupService.getOrderGroupById(anyLong())).thenReturn(OrderGroupResponse.fromEntity(orderGroup));
        when(orderDetailRepository.save(any())).thenReturn(orderDetail);

        OrderDetailResponse response = orderDetailService.createOrderDetail(
                new OrderDetailCreateRequest(
                        orderDetail.getBookId(),
                        orderDetail.getAmount(),
                        orderDetail.getPrice(),
                        1L,
                        1L));

        assertNotNull(response);
        assertEquals(1L, response.getBookId());
        assertEquals(3, response.getAmount());
        assertEquals(10000, response.getPrice());

        verify(orderDetailRepository, times(1)).save(any());
    }

    @Test
    void testCreateOrderDetail_Fail() {
        when(wrappingService.getWrappingById(anyLong())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> orderDetailService.createOrderDetail(
                new OrderDetailCreateRequest(
                        null,
                        -1,
                        0,
                        1L,
                        1L)));
    }

    @Test
    void testUpdateOrderDetailStatus_Success() {
        when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.of(orderDetail));

        OrderDetailResponse response = orderDetailService.updateOrderDetailStatus(
                1L,
                new OrderDetailUpdateStatusRequest(Status.PAYMENT_COMPLETED));

        assertNotNull(response);
        assertEquals(1L, response.getBookId());
        assertEquals(3, response.getAmount());
        assertEquals(10000, response.getPrice());
        assertEquals(Status.PAYMENT_COMPLETED, response.getStatus());

        verify(orderDetailRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateOrderDetailStatus_Fail() {
        when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderDetailService.updateOrderDetailStatus(
                1L,
                new OrderDetailUpdateStatusRequest(Status.PAYMENT_COMPLETED)));
    }

    @Test
    void testDeleteOrderDetail_Success() {
        when(orderDetailRepository.existsById(anyLong())).thenReturn(true);
        orderDetailService.deleteOrderDetail(1L);

        verify(orderDetailRepository, times(1)).deleteById(anyLong());
        verify(orderDetailRepository, times(1)).existsById(anyLong());
    }

    @Test
    void testDeleteOrderDetail_Fail() {
        when(orderDetailRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> orderDetailService.deleteOrderDetail(1L));
        verify(orderDetailRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void testGetOrderDetailsToList_Success() {
        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.ofCreate(2L, 2, 5000, wrapping, orderGroup);
        when(orderDetailRepository.findAllByOrderGroupId(anyLong())).thenReturn(List.of(orderDetail, orderDetail2));

        List<OrderDetailResponse> response = orderDetailService.getOrderDetailsToList(1L);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.getFirst().getBookId());
        assertEquals(3, response.getFirst().getAmount());
        assertEquals(10000, response.getFirst().getPrice());

        verify(orderDetailRepository, times(1)).findAllByOrderGroupId(anyLong());
    }

    @Test
    void testGetOrderDetailsToList_Fail() {
        when(orderDetailRepository.findAllByOrderGroupId(anyLong())).thenReturn(List.of());
        assertThrows(RuntimeException.class, () -> orderDetailService.getOrderDetailsToList(1L));
        verify(orderDetailRepository, times(1)).findAllByOrderGroupId(anyLong());
    }
}
