package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.notfound.OrderDetailNotFoundException;
import com.tripleseven.orderapi.exception.notfound.OrderGroupNotFoundException;
import com.tripleseven.orderapi.repository.orderdetail.OrderDetailRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderDetailServiceTest {
    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private OrderGroupRepository orderGroupRepository;

    @InjectMocks
    private OrderDetailServiceImpl orderDetailService;

    private Wrapping wrapping;

    private OrderGroup orderGroup;

    private OrderDetail orderDetail;

    @BeforeEach
    void setUp() {
        wrapping = new Wrapping();
        ReflectionTestUtils.setField(wrapping, "id", 1L);
        wrapping.ofCreate("Test Wrapping", 100);


        orderGroup = new OrderGroup();
        ReflectionTestUtils.setField(orderGroup, "id", 1L);
        orderGroup.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", 1000, "Test Address", wrapping);

        orderDetail = new OrderDetail();
        ReflectionTestUtils.setField(orderDetail, "id", 1L);
        orderDetail.ofCreate(1L, 3, 10000, 9000, orderGroup);
    }

    @Test
    void testGetOrderDetailById_Success() {
        when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.of(orderDetail));

        OrderDetailResponseDTO response = orderDetailService.getOrderDetailService(1L);

        assertNotNull(response);
        assertEquals(1L, response.getBookId());
        assertEquals(3, response.getAmount());
        assertEquals(10000, response.getPrimePrice());
        assertEquals(9000, response.getDiscountPrice());


        verify(orderDetailRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderDetailById_Fail() {
        when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderDetailNotFoundException.class, () -> orderDetailService.getOrderDetailService(1L));
        verify(orderDetailRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateOrderDetail_Success() {
        when(orderGroupRepository.findById(anyLong())).thenReturn(Optional.of(orderGroup));
        when(orderDetailRepository.save(any())).thenReturn(orderDetail);

        OrderDetailResponseDTO response = orderDetailService.createOrderDetail(
                new OrderDetailCreateRequestDTO(
                        orderDetail.getBookId(),
                        orderDetail.getAmount(),
                        orderDetail.getPrimePrice(),
                        orderDetail.getDiscountPrice(),
                        1L));

        assertNotNull(response);
        assertEquals(1L, response.getBookId());
        assertEquals(3, response.getAmount());
        assertEquals(10000, response.getPrimePrice());
        assertEquals(9000, response.getDiscountPrice());


        verify(orderDetailRepository, times(1)).save(any());
    }

    @Test
    void testCreateOrderDetail_Fail() {
        when(orderGroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(OrderGroupNotFoundException.class, () -> orderDetailService.createOrderDetail(
                new OrderDetailCreateRequestDTO(
                        2L,
                        100,
                        100,
                        100,
                        1L)));
    }

    @Test
    void testUpdateOrderDetailStatus_Success() {
        when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.of(orderDetail));

        List<OrderDetailResponseDTO> response = orderDetailService.updateOrderDetailStatus(
                List.of(1L,2L),
                OrderStatus.PAYMENT_COMPLETED);

        assertNotNull(response);
//        assertEquals(1L, response.getBookId());
//        assertEquals(3, response.getAmount());
//        assertEquals(10000, response.getPrimePrice());
//        assertEquals(9000, response.getDiscountPrice());
//
//        assertEquals(Status.PAYMENT_COMPLETED, response.getStatus());

        verify(orderDetailRepository, times(1)).findById(anyLong());
    }

    @Test
    void testUpdateOrderDetailStatus_Fail() {
        when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(OrderDetailNotFoundException.class, () -> orderDetailService.updateOrderDetailStatus(
                List.of(1L),
                OrderStatus.PAYMENT_COMPLETED));
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
        assertThrows(OrderDetailNotFoundException.class, () -> orderDetailService.deleteOrderDetail(1L));
        verify(orderDetailRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void testGetOrderDetailsToList_Success() {
        OrderDetail orderDetail2 = new OrderDetail();
        ReflectionTestUtils.setField(orderDetail2, "id", 2L);
        orderDetail2.ofCreate(2L, 2, 5000, 4000, orderGroup);
        when(orderDetailRepository.findAllByOrderGroupId(anyLong())).thenReturn(List.of(orderDetail, orderDetail2));

        List<OrderDetailResponseDTO> response = orderDetailService.getOrderDetailsToList(1L);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.getFirst().getBookId());
        assertEquals(3, response.getFirst().getAmount());
        assertEquals(10000, response.getFirst().getPrimePrice());
        assertEquals(9000, response.getFirst().getDiscountPrice());


        verify(orderDetailRepository, times(1)).findAllByOrderGroupId(anyLong());
    }

    @Test
    void testGetOrderDetailsToList_Fail() {
        when(orderDetailRepository.findAllByOrderGroupId(anyLong())).thenReturn(List.of());
        List<OrderDetailResponseDTO>  orderDetailsToList = orderDetailService.getOrderDetailsToList(1L);
        assertNotNull(orderDetailsToList);
        verify(orderDetailRepository, times(1)).findAllByOrderGroupId(anyLong());
    }
}
