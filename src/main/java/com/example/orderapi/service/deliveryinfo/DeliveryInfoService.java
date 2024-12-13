package com.example.orderapi.service.deliveryinfo;

import com.example.orderapi.dto.deliveryinfo.*;

public interface DeliveryInfoService {

    DeliveryInfoResponse getById(Long id);
    DeliveryInfoResponse create(DeliveryInfoCreateRequest deliveryInfoCreateRequest);
    DeliveryInfoResponse updateAll(Long id, DeliveryInfoUpdateRequest deliveryInfoUpdateRequest);
    DeliveryInfoResponse updateLogistics(Long id, DeliveryInfoLogisticsUpdateRequest deliveryInfoLogisticsUpdateRequest);
    DeliveryInfoResponse updateArrivedAt(Long id, DeliveryInfoArrivedAtUpdateRequest deliveryInfoArrivedAtUpdateRequest);
    void delete(Long id);
}
