package com.example.orderapi.service;

import com.example.orderapi.dto.deliveryinfo.DeliveryInfoArrivedAtUpdateRequest;
import com.example.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequest;
import com.example.orderapi.dto.deliveryinfo.DeliveryInfoLogisticsUpdateRequest;
import com.example.orderapi.dto.deliveryinfo.DeliveryInfoResponse;
import com.example.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.example.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import com.example.orderapi.service.deliveryinfo.DeliveryInfoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryInfoServiceTest {
    @Mock
    private DeliveryInfoRepository deliveryInfoRepository;

    @InjectMocks
    private DeliveryInfoServiceImpl deliveryInfoService;

    private DeliveryInfo deliveryInfo;

    private ZonedDateTime forwardedAt;

    private LocalDate deliveryDate;

    private ZonedDateTime arrivedAt;

    @BeforeEach
    void setUp() {
        deliveryInfo = new DeliveryInfo();
        deliveryInfo.ofCreate("Test DeliveryInfo", 12345678);
        forwardedAt = ZonedDateTime.parse("2024-12-15T10:30:00+09:00[Asia/Seoul]");
        deliveryDate = LocalDate.parse("2024-12-15");
        arrivedAt = ZonedDateTime.parse("2024-12-17T11:24:00+09:00[Asia/Seoul]");
    }

    @Test
    void testFindById_Success() {
        when(deliveryInfoRepository.findById(anyLong())).thenReturn(Optional.of(deliveryInfo));

        DeliveryInfoResponse response = deliveryInfoService.getDeliveryInfoById(1L);

        assertNotNull(response);
        assertEquals("Test DeliveryInfo", response.getName());
        assertEquals(12345678, response.getInvoiceNumber());

        verify(deliveryInfoRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_Fail() {
        when(deliveryInfoRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> deliveryInfoService.getDeliveryInfoById(1L));
        verify(deliveryInfoRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateDeliveryInfo_Success() {
        when(deliveryInfoRepository.save(any())).thenReturn(deliveryInfo);

        DeliveryInfoResponse response = deliveryInfoService.createDeliveryInfo(
                new DeliveryInfoCreateRequest(
                        "Test DeliveryInfo",
                        12345678));

        assertNotNull(response);
        assertEquals("Test DeliveryInfo", response.getName());
        assertEquals(12345678, response.getInvoiceNumber());

        verify(deliveryInfoRepository, times(1)).save(any());
    }

    @Test
    void testCreateDeliveryInfo_Fail() {
        assertThrows(RuntimeException.class, () -> deliveryInfoService.createDeliveryInfo(
                new DeliveryInfoCreateRequest(
                        null,
                        0)));
    }

    @Test
    void testUpdateLogisticsDeliveryInfo_Success() {
        DeliveryInfoLogisticsUpdateRequest updateRequest = new DeliveryInfoLogisticsUpdateRequest(forwardedAt, deliveryDate);

        when(deliveryInfoRepository.findById(1L)).thenReturn(Optional.of(deliveryInfo));

        DeliveryInfoResponse response = deliveryInfoService.updateDeliveryInfoLogistics(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Test DeliveryInfo", response.getName());
        assertEquals(12345678, response.getInvoiceNumber());
        assertEquals(forwardedAt, response.getForwardedAt());
        assertEquals(deliveryDate, response.getDeliveryDate());
        assertNull(response.getArrivedAt());

        verify(deliveryInfoRepository, times(1)).findById(1L);

    }

    @Test
    void testUpdateLogisticsDeliveryInfo_Fail() {
        DeliveryInfoLogisticsUpdateRequest updateRequest = new DeliveryInfoLogisticsUpdateRequest(forwardedAt, deliveryDate);
        when(deliveryInfoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> deliveryInfoService.updateDeliveryInfoLogistics(1L, updateRequest));
        verify(deliveryInfoRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateArrivedDeliveryInfo_Success() {
        DeliveryInfoArrivedAtUpdateRequest updateRequest = new DeliveryInfoArrivedAtUpdateRequest(arrivedAt);
        when(deliveryInfoRepository.findById(1L)).thenReturn(Optional.of(deliveryInfo));

        DeliveryInfoResponse response = deliveryInfoService.updateDeliveryInfoArrivedAt(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Test DeliveryInfo", response.getName());
        assertEquals(12345678, response.getInvoiceNumber());
        assertNull(response.getForwardedAt());
        assertNull(response.getDeliveryDate());
        assertEquals(arrivedAt, response.getArrivedAt());

        verify(deliveryInfoRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateArrivedDeliveryInfo_Fail() {
        DeliveryInfoArrivedAtUpdateRequest updateRequest = new DeliveryInfoArrivedAtUpdateRequest(arrivedAt);
        when(deliveryInfoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> deliveryInfoService.updateDeliveryInfoArrivedAt(1L, updateRequest));
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
        assertThrows(RuntimeException.class, () -> deliveryInfoService.deleteDeliveryInfo(1L));
        verify(deliveryInfoRepository, times(0)).deleteById(1L);
    }
}
