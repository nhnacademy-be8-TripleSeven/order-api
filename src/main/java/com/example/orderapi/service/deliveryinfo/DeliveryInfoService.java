package com.example.orderapi.service.deliveryinfo;

import com.example.orderapi.dto.deliveryinfo.DeliveryInfoArrivedAtUpdateRequest;
import com.example.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequest;
import com.example.orderapi.dto.deliveryinfo.DeliveryInfoLogisticsUpdateRequest;
import com.example.orderapi.dto.deliveryinfo.DeliveryInfoResponse;

public interface DeliveryInfoService {

    DeliveryInfoResponse getDeliveryInfoById(Long id);

    DeliveryInfoResponse createDeliveryInfo(DeliveryInfoCreateRequest deliveryInfoCreateRequest);

    DeliveryInfoResponse updateDeliveryInfoLogistics(Long id, DeliveryInfoLogisticsUpdateRequest deliveryInfoLogisticsUpdateRequest);

    DeliveryInfoResponse updateDeliveryInfoArrivedAt(Long id, DeliveryInfoArrivedAtUpdateRequest deliveryInfoArrivedAtUpdateRequest);

    void deleteDeliveryInfo(Long id);
}
