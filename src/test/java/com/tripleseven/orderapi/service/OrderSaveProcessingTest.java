package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.business.feign.BookService;
import com.tripleseven.orderapi.business.order.process.OrderSaveProcessing;
import com.tripleseven.orderapi.business.point.PointService;
import com.tripleseven.orderapi.business.rabbit.RabbitService;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponseDTO;
import com.tripleseven.orderapi.dto.order.AddressInfoDTO;
import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import com.tripleseven.orderapi.dto.order.RecipientInfoDTO;
import com.tripleseven.orderapi.dto.orderdetail.OrderDetailResponseDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupResponseDTO;
import com.tripleseven.orderapi.dto.pay.PayCancelRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.PaymentDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pay.PaymentStatus;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.exception.ErrorCode;
import com.tripleseven.orderapi.service.deliveryinfo.DeliveryInfoService;
import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import com.tripleseven.orderapi.service.ordergroup.OrderGroupService;
import com.tripleseven.orderapi.service.pay.PayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderSaveProcessingTest {

    @Mock
    private OrderGroupService orderGroupService;

    @Mock
    private OrderDetailService orderDetailService;

    @Mock
    private DeliveryInfoService deliveryInfoService;

    @Mock
    private PointService pointService;

    @Mock
    private PayService payService;

    @Mock
    private BookService bookService;

    @Mock
    private RabbitService rabbitService;

    @Mock
    private HashOperations<String, String, PayInfoDTO> hashOperations;

    @Mock
    private RedisTemplate<String, PayInfoDTO> redisTemplate;


    @InjectMocks
    private OrderSaveProcessing orderSaveProcessing;

    private PayInfoDTO payInfo;
    private PaymentDTO paymentDTO;
    private OrderGroup orderGroup;
    private DeliveryInfo deliveryInfo;
    private OrderDetail orderDetail;

    @BeforeEach
    void setUp() {
        Wrapping wrapping = new Wrapping();
        ReflectionTestUtils.setField(wrapping, "id", 1L);
        wrapping.ofCreate("Test Wrapping", 100);

        orderGroup = new OrderGroup();
        ReflectionTestUtils.setField(orderGroup, "id", 1L);
        orderGroup.ofCreate(1L,
                "Test Ordered",
                "Test Recipient",
                "01012345678",
                "01012345678",
                1000,
                "Test Address",
                wrapping);

        deliveryInfo = new DeliveryInfo();
        ReflectionTestUtils.setField(deliveryInfo, "id", 1L);

        deliveryInfo.ofCreate(orderGroup);
        OrderBookInfoDTO orderBookInfoDTO =
                new OrderBookInfoDTO(
                        1L,
                        "title",
                        10000,
                        2,
                        1L,
                        1000
                );

        orderDetail = new OrderDetail();
        ReflectionTestUtils.setField(orderDetail, "id", 1L);
        orderDetail.ofCreate(1L, 3, 10000, 9000, orderGroup);

        RecipientInfoDTO recipientInfoDTO = new RecipientInfoDTO("name","01012345678","01012345678");
        AddressInfoDTO addressInfoDTO = new AddressInfoDTO("road","zone","detail");
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
                        null,
                        1000,
                        10000
                )
        );

        paymentDTO = new PaymentDTO(1L, LocalDate.now(), 1000, PaymentStatus.DONE, "PAY1234");

        when(redisTemplate.opsForHash()).thenAnswer(invocation -> hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(payInfo);
    }

    @Test
    void testProcessNonMemberOrder_Success() {
        when(orderGroupService.createOrderGroup(any(), any(OrderGroupCreateRequestDTO.class)))
                .thenReturn(OrderGroupResponseDTO.fromEntity(orderGroup));

        when(deliveryInfoService.createDeliveryInfo(any(DeliveryInfoCreateRequestDTO.class))).thenReturn(DeliveryInfoResponseDTO.fromEntity(deliveryInfo));
        when(orderDetailService.createOrderDetail(any())).thenReturn(OrderDetailResponseDTO.fromEntity(orderDetail));
        doNothing().when(payService).createPay(any(PaymentDTO.class), anyLong());
        doNothing().when(rabbitService).sendCartMessage(anyString(), anyList());

        orderSaveProcessing.processNonMemberOrder("guest123", paymentDTO);


        verify(orderGroupService, times(1)).createOrderGroup(eq(OrderSaveProcessing.GUEST_USER_ID), any(OrderGroupCreateRequestDTO.class));
        verify(deliveryInfoService, times(1)).createDeliveryInfo(any(DeliveryInfoCreateRequestDTO.class));
        verify(orderDetailService, times(1)).createOrderDetail(any());
        verify(payService, times(1)).createPay(paymentDTO, 1L);
        verify(rabbitService, times(1)).sendCartMessage(anyString(), anyList());
    }

    @Test
    void testProcessNonMemberOrder_Fail_PaymentCancellation() throws IOException {
        when(orderGroupService.createOrderGroup(any(), any(OrderGroupCreateRequestDTO.class)))
                .thenThrow(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));

        assertThrows(CustomException.class, () -> orderSaveProcessing.processNonMemberOrder("guest123", paymentDTO));

        verify(payService, times(1)).cancelRequest(eq("PAY1234"), any(PayCancelRequestDTO.class));
    }

    @Test
    void testProcessMemberOrder_Success() {
        when(orderGroupService.createOrderGroup(anyLong(), any(OrderGroupCreateRequestDTO.class)))
                .thenReturn(OrderGroupResponseDTO.fromEntity(orderGroup));

        when(deliveryInfoService.createDeliveryInfo(any(DeliveryInfoCreateRequestDTO.class))).thenReturn(DeliveryInfoResponseDTO.fromEntity(deliveryInfo));
        when(orderDetailService.createOrderDetail(any())).thenReturn(OrderDetailResponseDTO.fromEntity(orderDetail));
        doNothing().when(payService).createPay(any(PaymentDTO.class), anyLong());
        doNothing().when(rabbitService).sendCartMessage(anyString(), anyList());
        doNothing().when(rabbitService).sendPointMessage(anyLong(), anyLong(), anyLong());

        orderSaveProcessing.processMemberOrder(1L, paymentDTO);

        verify(orderGroupService, times(1)).createOrderGroup(eq(1L), any(OrderGroupCreateRequestDTO.class));
        verify(deliveryInfoService, times(1)).createDeliveryInfo(any(DeliveryInfoCreateRequestDTO.class));
        verify(orderDetailService, times(1)).createOrderDetail(any());
        verify(payService, times(1)).createPay(paymentDTO, 1L);
        verify(rabbitService, times(1)).sendCartMessage(anyString(), anyList());
        verify(rabbitService, times(1)).sendPointMessage(eq(1L), eq(1L), anyLong());
    }

    @Test
    void testProcessMemberOrder_Fail_PaymentCancellation() throws IOException {
        when(orderGroupService.createOrderGroup(anyLong(), any(OrderGroupCreateRequestDTO.class)))
                .thenThrow(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));

        assertThrows(CustomException.class, () -> orderSaveProcessing.processMemberOrder(1L, paymentDTO));

        verify(payService, times(1)).cancelRequest(eq("PAY1234"), any(PayCancelRequestDTO.class));
    }

    @Test
    void testHandlePaymentCancellation_Success() throws IOException {
        when(payService.cancelRequest(anyString(), any(PayCancelRequestDTO.class))).thenReturn(OrderGroupResponseDTO.fromEntity(orderGroup));

        assertThrows(Exception.class, () -> orderSaveProcessing.processNonMemberOrder("guest123", paymentDTO));

        verify(payService, times(1)).cancelRequest(anyString(), any(PayCancelRequestDTO.class));
    }
}