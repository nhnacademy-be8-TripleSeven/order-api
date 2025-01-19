package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.client.BookCouponApiClient;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailCreateRequestDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import com.tripleseven.orderapi.repository.orderdetail.OrderDetailRepository;
import com.tripleseven.orderapi.repository.orderdetail.querydsl.QueryDslOrderDetailRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailServiceImpl;
import com.tripleseven.orderapi.service.ordergrouppointhistory.OrderGroupPointHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDetailServiceTest {
    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private OrderGroupRepository orderGroupRepository;

    @Mock
    private DeliveryInfoRepository deliveryInfoRepository;

    @Mock
    private BookCouponApiClient bookCouponApiClient;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private OrderGroupPointHistoryService orderGroupPointHistoryService;

    @Mock
    private QueryDslOrderDetailRepository queryDslOrderDetailRepository;

    @InjectMocks
    private OrderDetailServiceImpl orderDetailService;

    private Wrapping wrapping;

    private OrderGroup orderGroup;

    private OrderDetail orderDetail;

    private PointHistory pointHistory;

    @BeforeEach
    void setUp() {
        wrapping = new Wrapping();
        ReflectionTestUtils.setField(wrapping, "id", 1L);
        wrapping.ofCreate("Test Wrapping", 100);


        orderGroup = new OrderGroup();
        ReflectionTestUtils.setField(orderGroup, "id", 1L);
        orderGroup.ofCreate(1L, "Test Ordered", "Test Recipient", "01012345678", "01012345678", 1000, "Test Address", wrapping);

        orderDetail = new OrderDetail();
        ReflectionTestUtils.setField(orderDetail, "id", 1L);
        orderDetail.ofCreate(1L, 3, 10000, 9000, orderGroup);

        pointHistory = PointHistory.ofCreate(
                HistoryTypes.EARN,
                10,
                "",
                1L
        );
        ReflectionTestUtils.setField(pointHistory, "id", 1L);
    }

    @Test
    void testGetOrderDetailById_Success() {
        when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.of(orderDetail));

        OrderDetailResponseDTO response = orderDetailService.getOrderDetailService(1L);

        assertNotNull(response);
        assertEquals(1L, response.getBookId());
        assertEquals(3, response.getQuantity());
        assertEquals(10000, response.getPrimePrice());
        assertEquals(9000, response.getDiscountPrice());


        verify(orderDetailRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderDetailById_Fail() {
        when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> orderDetailService.getOrderDetailService(1L));
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
        assertEquals(3, response.getQuantity());
        assertEquals(10000, response.getPrimePrice());
        assertEquals(9000, response.getDiscountPrice());


        verify(orderDetailRepository, times(1)).save(any());
    }

    @Test
    void testCreateOrderDetail_Fail() {
        OrderDetailCreateRequestDTO requestDTO = new OrderDetailCreateRequestDTO(2L, 100, 100, 100, 1L);

        when(orderGroupRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> orderDetailService.createOrderDetail(requestDTO));

        assertNotNull(exception);
        verify(orderGroupRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateOrderDetailStatus_Success() {
        OrderDetail orderDetail1 = new OrderDetail();
        ReflectionTestUtils.setField(orderDetail1, "id", 1L);
        orderDetail1.ofCreate(1L, 2, 5000, 4500, orderGroup);
        orderDetail1.ofUpdateStatus(OrderStatus.PAYMENT_PENDING);

        OrderDetail orderDetail2 = new OrderDetail();
        ReflectionTestUtils.setField(orderDetail2, "id", 2L);
        orderDetail2.ofCreate(2L, 1, 3000, 2800, orderGroup);
        orderDetail2.ofUpdateStatus(OrderStatus.PAYMENT_PENDING);

        List<Long> ids = List.of(1L, 2L);

        PointHistory pointHistory = PointHistory.ofCreate(
                HistoryTypes.EARN,
                1000,
                "",
                1L
        );

        ReflectionTestUtils.setField(pointHistory, "id", 1L);

        when(orderDetailRepository.findById(1L)).thenReturn(Optional.of(orderDetail1));
        when(orderDetailRepository.findById(2L)).thenReturn(Optional.of(orderDetail2));
        when(pointHistoryRepository.save(any())).thenReturn(pointHistory);

        List<OrderDetailResponseDTO> response = orderDetailService.updateOrderDetailStatus(ids, OrderStatus.ORDER_CANCELED);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(OrderStatus.ORDER_CANCELED, response.get(0).getOrderStatus());
        assertEquals(OrderStatus.ORDER_CANCELED, response.get(1).getOrderStatus());

        verify(orderDetailRepository, times(2)).findById(anyLong());
    }

    @Test
    void testUpdateOrderDetailStatus_Fail() {
        List<Long> orderIds = List.of(1L);
        OrderStatus status = OrderStatus.PAYMENT_COMPLETED;

        CustomException exception = assertThrows(CustomException.class,
                () -> orderDetailService.updateOrderDetailStatus(orderIds, status));

        assertNotNull(exception);
    }

    @Test
    void testUpdateOrderDetailStatus_WithInvalidStatus() {
        OrderDetail orderDetail = new OrderDetail();
        ReflectionTestUtils.setField(orderDetail, "id", 1L);
        orderDetail.ofCreate(1L, 2, 5000, 4500, orderGroup);
        orderDetail.ofUpdateStatus(OrderStatus.SHIPPING);

        when(orderDetailRepository.findById(1L)).thenReturn(Optional.of(orderDetail));

        CustomException exception = assertThrows(CustomException.class,
                () -> orderDetailService.updateOrderDetailStatus(List.of(1L), OrderStatus.ORDER_CANCELED));

        assertNotNull(exception);
        assertEquals(ErrorCode.CANCEL_BAD_REQUEST, exception.getErrorCode());
        verify(orderDetailRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateOrderDetailStatus_WithExpiredReturn() {
        OrderDetail orderDetail = new OrderDetail();
        ReflectionTestUtils.setField(orderDetail, "id", 1L);
        orderDetail.ofCreate(1L, 2, 5000, 4500, orderGroup);
        orderDetail.ofUpdateStatus(OrderStatus.DELIVERED);

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        ReflectionTestUtils.setField(deliveryInfo, "orderGroup", orderGroup);
        deliveryInfo.ofShippingUpdate(LocalDate.now().minusDays(40));

        when(orderDetailRepository.findById(1L)).thenReturn(Optional.of(orderDetail));

        CustomException exception = assertThrows(CustomException.class,
                () -> orderDetailService.updateOrderDetailStatus(List.of(1L), OrderStatus.RETURNED_PENDING));

        assertNotNull(exception);
        assertEquals(ErrorCode.ID_NOT_FOUND, exception.getErrorCode());
        verify(orderDetailRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateOrderDetailStatus_WithPartialValidOrders() {
        OrderDetail validOrderDetail = new OrderDetail();
        ReflectionTestUtils.setField(validOrderDetail, "id", 1L);
        validOrderDetail.ofCreate(1L, 2, 5000, 4500, orderGroup);
        validOrderDetail.ofUpdateStatus(OrderStatus.PAYMENT_PENDING);

        OrderDetail invalidOrderDetail = new OrderDetail();
        ReflectionTestUtils.setField(invalidOrderDetail, "id", 2L);
        invalidOrderDetail.ofCreate(2L, 1, 3000, 2800, orderGroup);
        invalidOrderDetail.ofUpdateStatus(OrderStatus.SHIPPING);

        when(orderDetailRepository.findById(1L)).thenReturn(Optional.of(validOrderDetail));
        when(orderDetailRepository.findById(2L)).thenReturn(Optional.of(invalidOrderDetail));
        when(pointHistoryRepository.save(any())).thenReturn(pointHistory);

        CustomException exception = assertThrows(CustomException.class,
                () -> orderDetailService.updateOrderDetailStatus(List.of(1L, 2L), OrderStatus.ORDER_CANCELED));

        assertNotNull(exception);
        assertEquals(ErrorCode.CANCEL_BAD_REQUEST, exception.getErrorCode());
        verify(orderDetailRepository, times(2)).findById(anyLong());
    }

    @Test
    void testUpdateOrderDetailStatus_MultipleSuccess() {
        OrderDetail orderDetail1 = new OrderDetail();
        ReflectionTestUtils.setField(orderDetail1, "id", 1L);
        orderDetail1.ofCreate(1L, 2, 5000, 4500, orderGroup);
        orderDetail1.ofUpdateStatus(OrderStatus.PAYMENT_PENDING);

        OrderDetail orderDetail2 = new OrderDetail();
        ReflectionTestUtils.setField(orderDetail2, "id", 2L);
        orderDetail2.ofCreate(2L, 1, 3000, 2800, orderGroup);
        orderDetail2.ofUpdateStatus(OrderStatus.PAYMENT_COMPLETED);

        when(orderDetailRepository.findById(1L)).thenReturn(Optional.of(orderDetail1));
        when(orderDetailRepository.findById(2L)).thenReturn(Optional.of(orderDetail2));

        PointHistory pointHistory = PointHistory.ofCreate(HistoryTypes.EARN, 4500, "Test Refund", 1L);
        when(pointHistoryRepository.save(any())).thenReturn(pointHistory);

        List<OrderDetailResponseDTO> response = orderDetailService.updateOrderDetailStatus(
                List.of(1L, 2L), OrderStatus.ORDER_CANCELED);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(OrderStatus.ORDER_CANCELED, response.get(0).getOrderStatus());
        assertEquals(OrderStatus.ORDER_CANCELED, response.get(1).getOrderStatus());
        verify(orderDetailRepository, times(2)).findById(anyLong());
        verify(pointHistoryRepository, times(2)).save(any());
    }

    @Test
    void testUpdateOrderDetailStatus_NoOrdersFound() {
        when(orderDetailRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> orderDetailService.updateOrderDetailStatus(List.of(1L), OrderStatus.ORDER_CANCELED));

        assertNotNull(exception);
        assertEquals(ErrorCode.ID_NOT_FOUND, exception.getErrorCode());
        verify(orderDetailRepository, times(1)).findById(1L);
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
        assertThrows(CustomException.class, () -> orderDetailService.deleteOrderDetail(1L));
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
        assertEquals(3, response.getFirst().getQuantity());
        assertEquals(10000, response.getFirst().getPrimePrice());
        assertEquals(9000, response.getFirst().getDiscountPrice());


        verify(orderDetailRepository, times(1)).findAllByOrderGroupId(anyLong());
    }

    @Test
    void testGetOrderDetailsToList_Fail() {
        when(orderDetailRepository.findAllByOrderGroupId(anyLong())).thenReturn(List.of());
        List<OrderDetailResponseDTO> orderDetailsToList = orderDetailService.getOrderDetailsToList(1L);
        assertNotNull(orderDetailsToList);
        verify(orderDetailRepository, times(1)).findAllByOrderGroupId(anyLong());
    }

    @Test
    void testUpdateAdminOrderDetailStatus_Success() {
        when(orderDetailRepository.findById(1L)).thenReturn(Optional.of(orderDetail));
        when(orderGroupRepository.findById(1L)).thenReturn(Optional.of(orderGroup));
        when(deliveryInfoRepository.findById(1L)).thenReturn(Optional.of(new DeliveryInfo()));
        when(bookCouponApiClient.getBookName(1L)).thenReturn("Test Book");

        PointHistory pointHistory = PointHistory.ofCreate(
                HistoryTypes.EARN, 9000, "Refund: Test Book", orderGroup.getUserId());
        when(pointHistoryRepository.save(any())).thenReturn(pointHistory);

        List<OrderDetailResponseDTO> response = orderDetailService.updateAdminOrderDetailStatus(
                List.of(1L),
                OrderStatus.RETURNED);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(OrderStatus.RETURNED, response.get(0).getOrderStatus());

        verify(orderDetailRepository, times(1)).findById(1L);
        verify(pointHistoryRepository, times(1)).save(any());
    }

    @Test
    void testUpdateAdminOrderDetailStatus_Fail() {
        List<Long> orderIds = List.of(1L);
        OrderStatus newStatus = OrderStatus.RETURNED;

        when(orderDetailRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> orderDetailService.updateAdminOrderDetailStatus(orderIds, newStatus));

        assertNotNull(exception);
        verify(orderDetailRepository, times(1)).findById(1L);
    }

    @Test
    void testHasUserPurchasedBook_Success() {
        when(orderDetailRepository.existsByOrderGroupUserIdAndBookId(1L, 1L))
                .thenReturn(true);

        boolean result = orderDetailService.hasUserPurchasedBook(1L, 1L);

        assertTrue(result);
        verify(orderDetailRepository, times(1))
                .existsByOrderGroupUserIdAndBookId(1L, 1L);
    }

    @Test
    void testHasUserPurchasedBook_Fail() {
        when(orderDetailRepository.existsByOrderGroupUserIdAndBookId(1L, 1L))
                .thenReturn(false);

        boolean result = orderDetailService.hasUserPurchasedBook(1L, 1L);

        assertFalse(result);
        verify(orderDetailRepository, times(1))
                .existsByOrderGroupUserIdAndBookId(1L, 1L);
    }


    @Test
    void testGetNetTotalByPeriod_Success() {
        when(queryDslOrderDetailRepository.computeNetTotal(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(5000L);

        Long netTotal = orderDetailService.getNetTotalByPeriod(1L, LocalDate.now().minusDays(10), LocalDate.now());

        assertNotNull(netTotal);
        assertEquals(5000L, netTotal);

        verify(queryDslOrderDetailRepository, times(1))
                .computeNetTotal(eq(1L), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testGetNetTotalByPeriod_Fail() {
        when(queryDslOrderDetailRepository.computeNetTotal(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(0L);

        Long netTotal = orderDetailService.getNetTotalByPeriod(1L, LocalDate.now().minusDays(10), LocalDate.now());

        assertNotNull(netTotal);
        assertEquals(0L, netTotal); // Null 대신 기본값 반환하도록 설정된 경우

        verify(queryDslOrderDetailRepository, times(1))
                .computeNetTotal(eq(1L), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testCompleteOverdueShipments_Success() {
        List<OrderDetail> overdueOrderDetails = List.of(
                orderDetail,
                orderDetail
        );

        when(orderDetailRepository.findByOrderStatusAndUpdateDateBefore(eq(OrderStatus.SHIPPING), any(LocalDate.class)))
                .thenReturn(overdueOrderDetails);

        orderDetailService.completeOverdueShipments(Duration.ofDays(2));

        for (OrderDetail orderDetail : overdueOrderDetails) {
            assertEquals(OrderStatus.DELIVERED, orderDetail.getOrderStatus());
        }

        verify(orderDetailRepository, times(1))
                .findByOrderStatusAndUpdateDateBefore(eq(OrderStatus.SHIPPING), any(LocalDate.class));
    }

    @Test
    void testCompleteOverdueShipments_NoOverdueOrders() {
        when(orderDetailRepository.findByOrderStatusAndUpdateDateBefore(eq(OrderStatus.SHIPPING), any(LocalDate.class)))
                .thenReturn(List.of());

        orderDetailService.completeOverdueShipments(Duration.ofHours(2));

        verify(orderDetailRepository, times(1))
                .findByOrderStatusAndUpdateDateBefore(eq(OrderStatus.SHIPPING), any(LocalDate.class));
    }
}
