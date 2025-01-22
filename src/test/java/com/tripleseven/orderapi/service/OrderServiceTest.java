package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.business.feign.BookService;
import com.tripleseven.orderapi.business.order.OrderServiceImpl;
import com.tripleseven.orderapi.business.point.PointService;
import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponseDTO;
import com.tripleseven.orderapi.dto.order.*;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.PaymentDTO;
import com.tripleseven.orderapi.dto.pointhistory.PointHistoryResponseDTO;
import com.tripleseven.orderapi.dto.pointpolicy.PointPolicyResponseDTO;
import com.tripleseven.orderapi.dto.wrapping.WrappingResponseDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pay.PaymentStatus;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

    @Mock
    private BookService bookService;

    @Mock
    private PointService pointService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Wrapping wrapping;

    private OrderGroup orderGroup;

    private OrderGroup orderGroup2;

    private OrderDetail orderDetail;

    private OrderDetail orderDetail2;

    private DeliveryInfo deliveryInfo;

    private PayInfoDTO payInfo;

    private PaymentDTO payment;

    private PointHistory pointHistory;

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


        pointHistory = PointHistory.ofCreate(HistoryTypes.EARN, 100, "Earned Points", 1L);
        ReflectionTestUtils.setField(pointHistory, "id", 1L);

        Wrapping wrapping = new Wrapping();
        ReflectionTestUtils.setField(wrapping, "id", 1L);
        wrapping.ofCreate("Test Wrapping", 100);

        orderGroup2 = new OrderGroup();
        ReflectionTestUtils.setField(orderGroup2, "id", 1L);
        orderGroup2.ofCreate(1L,
                "Test Ordered",
                "Test Recipient",
                "01012345678",
                "01012345678",
                1000,
                "Test Address",
                wrapping);

        deliveryInfo = new DeliveryInfo();
        ReflectionTestUtils.setField(deliveryInfo, "id", 1L);

        deliveryInfo.ofCreate(orderGroup, LocalDate.now());
        OrderBookInfoDTO orderBookInfoDTO =
                new OrderBookInfoDTO(
                        1L,
                        "title",
                        10000,
                        2,
                        1L,
                        1000
                );

        orderDetail2 = new OrderDetail();
        ReflectionTestUtils.setField(orderDetail2, "id", 1L);
        orderDetail2.ofCreate(1L, 3, 10000, 9000, orderGroup);

        RecipientInfoDTO recipientInfoDTO = new RecipientInfoDTO("name", "01012345678", "01012345678");
        AddressInfoDTO addressInfoDTO = new AddressInfoDTO("road", "zone", "detail");
        payInfo = new PayInfoDTO();
        payInfo.ofCreate(
                1L,
                new PayInfoRequestDTO(
                        List.of(orderBookInfoDTO),
                        recipientInfoDTO,
                        addressInfoDTO,
                        LocalDate.now(),
                        "orderer",
                        "payType",
                        1000,
                        null,
                        1000,
                        10000
                )
        );

        payment = new PaymentDTO(1L, LocalDate.now(), 1000, PaymentStatus.DONE, "PAY1234");

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

    @Test
    void testGetOrderPayDetailAdmin_Success() {
        Long orderGroupId = 1L;

        List<OrderDetailResponseDTO> orderDetailResponses = List.of(
                OrderDetailResponseDTO.fromEntity(orderDetail)
        );

        when(orderDetailService.getOrderDetailsToList(orderGroupId)).thenReturn(orderDetailResponses);

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

        OrderPayDetailDTO result = orderService.getOrderPayDetailAdmin(orderGroupId);

        assertNotNull(result);
        assertEquals(1, result.getOrderInfos().size());
        assertEquals("Gift Wrap", result.getOrderGroupInfoDTO().getWrappingName());
        assertEquals("Jane Doe", result.getDeliveryInfo().getRecipientName());
        assertEquals("Toss", result.getOrderPayInfoDTO().getPaymentName());

        verify(orderDetailService, times(2)).getOrderDetailsToList(orderGroupId);
        verify(orderGroupService, times(1)).getOrderGroupById(orderGroupId);
        verify(wrappingService, times(1)).getWrappingById(1L);
        verify(orderGroupPointHistoryService, times(1)).getUsedPoint(orderGroupId);
        verify(orderGroupPointHistoryService, times(1)).getEarnedPoint(orderGroupId);
        verify(deliveryInfoService, times(1)).getDeliveryInfoDTO(orderGroupId);
        verify(payService, times(1)).getOrderPayInfo(orderGroupId);
    }

    @Test
    void testGetThreeMonthsNetAmount_Success() {
        Long userId = 1L;

        LocalDate today = LocalDate.now();
        LocalDate threeMonthsAgo = today.minusMonths(3);

        when(orderDetailService.getNetTotalByPeriod(userId, threeMonthsAgo, today)).thenReturn(15000L);

        Long netAmount = orderService.getThreeMonthsNetAmount(userId);

        assertNotNull(netAmount);
        assertEquals(15000L, (long) netAmount);

        verify(orderDetailService, times(1)).getNetTotalByPeriod(userId, threeMonthsAgo, today);
    }

    @Test
    void testGetThreeMonthsNetAmount_NoTransactions() {
        Long userId = 1L;

        LocalDate today = LocalDate.now();
        LocalDate threeMonthsAgo = today.minusMonths(3);

        when(orderDetailService.getNetTotalByPeriod(userId, threeMonthsAgo, today)).thenReturn(0L);

        Long netAmount = orderService.getThreeMonthsNetAmount(userId);

        assertNotNull(netAmount);
        assertEquals(0L, (long) netAmount);

        verify(orderDetailService, times(1)).getNetTotalByPeriod(userId, threeMonthsAgo, today);
    }

    @Test
    void testSaveOrderInfo_Success() {
        Long userId = 1L;
        Long orderGroupId = 1L;

        List<OrderBookInfoDTO> bookInfos = List.of(
                new OrderBookInfoDTO(1L, "",0,0,123L,0) // CouponId = 123L
        );
        ReflectionTestUtils.setField(payInfo, "bookOrderDetails", bookInfos);

        when(orderGroupService.createOrderGroup(any(), any()))
                .thenReturn(OrderGroupResponseDTO.fromEntity(orderGroup));
        when(deliveryInfoService.createDeliveryInfo(any())).thenReturn(DeliveryInfoResponseDTO.fromEntity(deliveryInfo));
        when(orderDetailService.createOrderDetail(any())).thenReturn(OrderDetailResponseDTO.fromEntity(orderDetail));
        doNothing().when(bookService).useCoupon(anyLong());
        doNothing().when(payService).createPay(eq(payment), eq(orderGroupId), anyString());
        when(pointService.createPointHistoryForPaymentSpend(anyLong(),anyLong(),anyLong())).thenReturn(PointHistoryResponseDTO.fromEntity(pointHistory));

        Long result = orderService.saveOrderInfo(
                userId,
                payInfo,
                payment,
                new OrderGroupCreateRequestDTO(
                        wrapping.getId(),
                        "",
                        "",
                        "",
                        "",
                        0,
                        ""
                ));

        assertNotNull(result);
        assertEquals(orderGroupId, result);

        verify(orderGroupService, times(1)).createOrderGroup(any(), any());
        verify(deliveryInfoService, times(1)).createDeliveryInfo(any(DeliveryInfoCreateRequestDTO.class));
        verify(orderDetailService, times(1)).createOrderDetail(any(OrderDetailCreateRequestDTO.class));
        verify(bookService, times(1)).useCoupon(123L);
        verify(payService, times(1)).createPay(eq(payment), eq(orderGroupId), anyString());
        verify(pointService, times(1)).createPointHistoryForPaymentSpend(anyLong(),anyLong(),anyLong());
    }

    @Test
    void testSaveOrderInfo_Failed_OrderGroupCreation() {
        Long userId = 1L;

        when(orderGroupService.createOrderGroup(any(), any()))
                .thenThrow(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));

        assertThrows(CustomException.class, () -> orderService.saveOrderInfo(
                userId,
                payInfo,
                payment,
                new OrderGroupCreateRequestDTO(
                        wrapping.getId(),
                        "",
                        "",
                        "",
                        "",
                        0,
                        ""
                )));

        verify(orderGroupService, times(1)).createOrderGroup(eq(userId), any());
        verify(deliveryInfoService, never()).createDeliveryInfo(any(DeliveryInfoCreateRequestDTO.class));
        verify(orderDetailService, never()).createOrderDetail(any(OrderDetailCreateRequestDTO.class));
        verify(bookService, never()).useCoupon(anyLong());
        verify(payService, never()).createPay(eq(payment), anyLong(), anyString());
        verify(pointService, never()).createPointHistoryForPaymentSpend(anyLong(), anyLong(), anyLong());
    }

    @Test
    void testSaveOrderInfo_Failed_CouponUse() {
        Long userId = 1L;
        Long orderGroupId = 1L;

        List<OrderBookInfoDTO> bookInfos = List.of(
                new OrderBookInfoDTO(1L, "",0,0,123L,0) // CouponId = 123L
        );
        ReflectionTestUtils.setField(payInfo, "bookOrderDetails", bookInfos);
        when(orderGroupService.createOrderGroup(any(), any()))
                .thenReturn(OrderGroupResponseDTO.fromEntity(orderGroup));
        when(deliveryInfoService.createDeliveryInfo(any())).thenReturn(DeliveryInfoResponseDTO.fromEntity(deliveryInfo));
        when(orderDetailService.createOrderDetail(any(OrderDetailCreateRequestDTO.class))).thenReturn(OrderDetailResponseDTO.fromEntity(orderDetail));
        doThrow(new CustomException(ErrorCode.BAD_REQUEST)).when(bookService).useCoupon(anyLong());
        assertThrows(CustomException.class, () -> orderService.saveOrderInfo(
                userId,
                payInfo,
                payment,
                new OrderGroupCreateRequestDTO(
                        wrapping.getId(),
                        "",
                        "",
                        "",
                        "",
                        0,
                        ""
                )));

        verify(orderGroupService, times(1)).createOrderGroup(eq(userId), any());
        verify(deliveryInfoService, times(1)).createDeliveryInfo(any(DeliveryInfoCreateRequestDTO.class));
        verify(orderDetailService, times(1)).createOrderDetail(any(OrderDetailCreateRequestDTO.class));
        verify(bookService, times(1)).useCoupon(eq(123L));
        verify(payService, never()).createPay(eq(payment), anyLong(), anyString());
        verify(pointService, never()).createPointHistoryForPaymentSpend(anyLong(), anyLong(), anyLong());
    }

    @Test
    void testHandlePaymentCancellation_Success() throws IOException {
        when(payService.cancelRequest(any(), any())).thenReturn(payment);

        assertThrows(CustomException.class, () -> ReflectionTestUtils.invokeMethod(orderService, "handlePaymentCancellation", payment, "Test Reason"));

        verify(payService, times(1)).cancelRequest(any(), any());
    }

    @Test
    void testHandlePaymentCancellation_Failure() throws IOException {

        doThrow(new IOException("Test Failure")).when(payService).cancelRequest(eq(payment.getPaymentKey()), any());

        assertThrows(CustomException.class, () -> ReflectionTestUtils.invokeMethod(orderService, "handlePaymentCancellation", payment, "Test Reason"));

        verify(payService, times(1)).cancelRequest(eq(payment.getPaymentKey()), any());
    }
}
