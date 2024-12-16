package com.example.orderapi.service.deliveryinfo;

import com.example.orderapi.dto.deliveryinfo.*;

public interface DeliveryInfoService {

    DeliveryInfoResponse getDeliveryInfoById(Long id);
    DeliveryInfoResponse createDeliveryInfo(DeliveryInfoCreateRequest deliveryInfoCreateRequest);
    DeliveryInfoResponse updateDeliveryInfoLogistics(Long id, DeliveryInfoLogisticsUpdateRequest deliveryInfoLogisticsUpdateRequest);
    DeliveryInfoResponse updateDeliveryInfoArrivedAt(Long id, DeliveryInfoArrivedAtUpdateRequest deliveryInfoArrivedAtUpdateRequest);
    void deleteDeliveryInfo(Long id);
}
