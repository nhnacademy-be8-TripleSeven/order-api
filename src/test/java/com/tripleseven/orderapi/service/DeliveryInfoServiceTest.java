package com.tripleseven.orderapi.service;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponseDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoUpdateRequestDTO;
import com.tripleseven.orderapi.dto.order.DeliveryInfoDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.exception.CustomException;
import com.tripleseven.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import com.tripleseven.orderapi.repository.deliveryinfo.querydsl.QueryDslDeliveryInfoRepository;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import com.tripleseven.orderapi.service.deliveryinfo.DeliveryInfoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryInfoServiceTest {
    @Mock
    private DeliveryInfoRepository deliveryInfoRepository;

    @Mock
    private OrderGroupRepository orderGroupRepository;

    @Mock
    private QueryDslDeliveryInfoRepository queryDslDeliveryInfoRepository;
    @InjectMocks
    private DeliveryInfoServiceImpl deliveryInfoService;

    private DeliveryInfo deliveryInfo;

    private OrderGroup orderGroup;

    private LocalDate arrivedAt;


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

        arrivedAt = LocalDate.parse("2024-12-17");
    }

    @Test
    void testFindById_Success() {
        when(deliveryInfoRepository.findById(anyLong())).thenReturn(Optional.of(deliveryInfo));

        DeliveryInfoResponseDTO response = deliveryInfoService.getDeliveryInfoById(1L);

        assertNotNull(response);

        verify(deliveryInfoRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_Fail() {
        when(deliveryInfoRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> deliveryInfoService.getDeliveryInfoById(1L));
        verify(deliveryInfoRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateDeliveryInfo_Success() {
        when(orderGroupRepository.findById(anyLong())).thenReturn(Optional.of(orderGroup));
        when(deliveryInfoRepository.save(any())).thenReturn(deliveryInfo);

        DeliveryInfoResponseDTO response = deliveryInfoService.createDeliveryInfo(
                new DeliveryInfoCreateRequestDTO(
                        1L,
                        arrivedAt
                ));

        assertNotNull(response);


        verify(deliveryInfoRepository, times(1)).save(any());
    }

    @Test
    void testCreateDeliveryInfo_Fail() {
        DeliveryInfoCreateRequestDTO requestDTO = new DeliveryInfoCreateRequestDTO(1L, arrivedAt);

        CustomException exception = assertThrows(CustomException.class,
                () -> deliveryInfoService.createDeliveryInfo(requestDTO));

        assertNotNull(exception);
    }

    @Test
    void testUpdateArrivedDeliveryInfo_Success() {
        DeliveryInfoUpdateRequestDTO updateRequest = new DeliveryInfoUpdateRequestDTO("Test DeliveryInfo",
                12345678, arrivedAt);
        when(deliveryInfoRepository.findById(1L)).thenReturn(Optional.of(deliveryInfo));

        DeliveryInfoResponseDTO response = deliveryInfoService.updateDeliveryInfo(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Test DeliveryInfo", response.getName());
        assertEquals(12345678, response.getInvoiceNumber());
        assertEquals(arrivedAt, response.getArrivedAt());

        verify(deliveryInfoRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateArrivedDeliveryInfo_Fail() {
        DeliveryInfoUpdateRequestDTO updateRequest = new DeliveryInfoUpdateRequestDTO("Test DeliveryInfo",
                12345678, arrivedAt);
        when(deliveryInfoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> deliveryInfoService.updateDeliveryInfo(1L, updateRequest));
        verify(deliveryInfoRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteDeliveryInfo_Success() {
        when(deliveryInfoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(deliveryInfoRepository).deleteById(1L);

        deliveryInfoService.deleteDeliveryInfo(1L);

        verify(deliveryInfoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDeliveryInfo_Fail() {
        when(deliveryInfoRepository.existsById(1L)).thenReturn(false);
        assertThrows(CustomException.class, () -> deliveryInfoService.deleteDeliveryInfo(1L));
        verify(deliveryInfoRepository, times(0)).deleteById(1L);
    }

    @Test
    void testUpdateDeliveryInfoShippingAt_Success() {
        when(deliveryInfoRepository.findById(1L)).thenReturn(Optional.of(deliveryInfo));

        DeliveryInfoResponseDTO response = deliveryInfoService.updateDeliveryInfoShippingAt(1L);

        assertNotNull(response);
        assertEquals(LocalDate.now(), response.getShippingAt());

        verify(deliveryInfoRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateDeliveryInfoShippingAt_Fail() {
        when(deliveryInfoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> deliveryInfoService.updateDeliveryInfoShippingAt(1L));

        verify(deliveryInfoRepository, times(1)).findById(1L);
    }

    @Test
    void testGetDeliveryInfoDTO_Success() {
        DeliveryInfoDTO deliveryInfoDTO = new DeliveryInfoDTO(
                "Test Delivery Info", 12345678, LocalDate.now(), 1L, LocalDate.now(),
                "Test Orderer", "Test Recipient", "01012345678", "Test Address", null
        );

        when(queryDslDeliveryInfoRepository.getDeliveryInfo(1L)).thenReturn(deliveryInfoDTO);

        DeliveryInfoDTO response = deliveryInfoService.getDeliveryInfoDTO(1L);

        assertNotNull(response);
        assertEquals(deliveryInfoDTO.getDeliveryInfoName(), response.getDeliveryInfoName());
        assertEquals(deliveryInfoDTO.getOrderId(), response.getOrderId());

        verify(queryDslDeliveryInfoRepository, times(1)).getDeliveryInfo(1L);
    }

    @Test
    void testGetDeliveryInfoDTO_Fail() {
        when(queryDslDeliveryInfoRepository.getDeliveryInfo(1L)).thenReturn(null);

        DeliveryInfoDTO response = deliveryInfoService.getDeliveryInfoDTO(1L);

        assertNull(response);

        verify(queryDslDeliveryInfoRepository, times(1)).getDeliveryInfo(1L);
    }
}
