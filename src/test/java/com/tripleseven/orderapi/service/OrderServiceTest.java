package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.business.order.OrderServiceImpl;
import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.order.DeliveryInfoDTO;
import com.tripleseven.orderapi.dto.order.OrderPayDetailDTO;
import com.tripleseven.orderapi.dto.order.OrderPayInfoDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponseDTO;
import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.service.deliveryinfo.DeliveryInfoService;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.ordergrouppointhistory.OrderGroupPointHistoryService;
import com.tripleseven.orderapi.service.pay.PayService;
import com.tripleseven.orderapi.service.wrapping.WrappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDetailService orderDetailService;

    @Mock
    private OrderGroupService orderGroupService;

    @Mock
    private WrappingService wrappingService;

    @Mock
    private OrderGroupPointHistoryService orderGroupPointHistoryService;

    @Mock
    private DeliveryInfoService deliveryInfoService;

    @Mock
    private BookCouponApiClient bookCouponApiClient;

    @Mock
    private PayService payService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Wrapping wrapping;

    private OrderGroup orderGroup;

    private OrderDetail orderDetail;

    @BeforeEach
    void setUp() {
        wrapping = new Wrapping();
        ReflectionTestUtils.setField(wrapping, "id", 1L);
        wrapping.ofCreate("Gift Wrap", 500);


        orderGroup = new OrderGroup();
        ReflectionTestUtils.setField(orderGroup, "id", 1L);
        orderGroup.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", "01012345678", 1000, "Test Address", wrapping);

        orderDetail = new OrderDetail();
        ReflectionTestUtils.setField(orderDetail, "id", 1L);
        orderDetail.ofCreate(1L, 2, 11000, 1000, orderGroup);
    }

    @Test
    void testGetOrderPayDetail_Success() {
        Long orderGroupId = 1L;
        Long userId = 1L;

        OrderGroup orderGroup2 = new OrderGroup();
        orderGroup2.ofCreate(userId, "Test Ordered", "Test Recipient", "01012345678", "01012345678", 1000, "Test Address", wrapping);
        ReflectionTestUtils.setField(orderGroup2, "id", 1L);

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.ofCreate(2L, 1, 22000, 2000, orderGroup2);
        ReflectionTestUtils.setField(orderDetail2, "id", 2L);

        List<OrderDetailResponseDTO> orderDetailResponses = List.of(
                OrderDetailResponseDTO.fromEntity(orderDetail),
                OrderDetailResponseDTO.fromEntity(orderDetail2)
        );

        when(orderDetailService.getOrderDetailsToList(orderGroupId)).thenReturn(orderDetailResponses);
        when(bookCouponApiClient.getBookName(1L)).thenReturn("Book A");
        when(bookCouponApiClient.getBookName(2L)).thenReturn("Book B");

        OrderGroupResponseDTO orderGroupResponse = OrderGroupResponseDTO.fromEntity(orderGroup);
        when(orderGroupService.getOrderGroupById(orderGroupId)).thenReturn(orderGroupResponse);

        WrappingResponseDTO wrappingResponse = WrappingResponseDTO.fromEntity(wrapping);
        when(wrappingService.getWrappingById(1L)).thenReturn(wrappingResponse);

        when(orderGroupPointHistoryService.getUsedPoint(orderGroupId)).thenReturn(1000L);
        when(orderGroupPointHistoryService.getEarnedPoint(orderGroupId)).thenReturn(500L);

        DeliveryInfoDTO deliveryInfo = new DeliveryInfoDTO(
                "DeliveryInfo 1",
                12345678,
                LocalDate.now(),
                orderGroupId,
                LocalDate.now(),
                "John Doe",
                "Jane Doe",
                "010-1234-5678",
                "2023-01-01",
                LocalDate.now()
        );
        when(deliveryInfoService.getDeliveryInfoDTO(orderGroupId)).thenReturn(deliveryInfo);

        OrderPayInfoDTO payInfo = new OrderPayInfoDTO(30000, "T1234", "Toss", LocalDate.now());
        when(payService.getOrderPayInfo(orderGroupId)).thenReturn(payInfo);
        OrderPayDetailDTO result = orderService.getOrderPayDetail(userId, orderGroupId);

        assertNotNull(result);
        assertEquals(2, result.getOrderInfos().size());
        assertEquals("Gift Wrap", result.getOrderGroupInfoDTO().getWrappingName());
        assertEquals("Jane Doe", result.getDeliveryInfo().getRecipientName());
        assertEquals("Toss", result.getOrderPayInfoDTO().getPaymentName());

        verify(orderDetailService, times(2)).getOrderDetailsToList(orderGroupId);
        verify(orderGroupService, times(2)).getOrderGroupById(orderGroupId);
        verify(wrappingService, times(1)).getWrappingById(1L);
        verify(orderGroupPointHistoryService, times(1)).getUsedPoint(orderGroupId);
        verify(orderGroupPointHistoryService, times(1)).getEarnedPoint(orderGroupId);
        verify(deliveryInfoService, times(1)).getDeliveryInfoDTO(orderGroupId);
        verify(payService, times(1)).getOrderPayInfo(orderGroupId);
    }

    @Test
    void testGetOrderPayDetail_Failed() {
        Long orderGroupId = 1L;
        Long userId = 999L;

        when(orderGroupService.getOrderGroupById(orderGroupId)).thenReturn(OrderGroupResponseDTO.fromEntity(orderGroup));

        assertThrows(CustomException.class, () -> orderService.getOrderPayDetail(userId, orderGroupId));

        verify(orderGroupService, times(1)).getOrderGroupById(anyLong());
        verify(orderDetailService, never()).getOrderDetailsToList(anyLong());
        verify(wrappingService, never()).getWrappingById(anyLong());
        verify(orderGroupPointHistoryService, never()).getUsedPoint(anyLong());
        verify(orderGroupPointHistoryService, never()).getEarnedPoint(anyLong());
        verify(deliveryInfoService, never()).getDeliveryInfoDTO(anyLong());
        verify(payService, never()).getOrderPayInfo(anyLong());
    }
}
