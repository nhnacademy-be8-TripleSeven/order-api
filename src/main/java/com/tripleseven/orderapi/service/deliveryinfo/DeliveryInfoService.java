package com.tripleseven.orderapi.service.deliveryinfo;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoArrivedAtUpdateRequest;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequest;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponse;

public interface DeliveryInfoService {

    DeliveryInfoResponse getDeliveryInfoById(Long id);

    DeliveryInfoResponse createDeliveryInfo(DeliveryInfoCreateRequest deliveryInfoCreateRequest);

    DeliveryInfoResponse updateDeliveryInfoArrivedAt(Long id, DeliveryInfoArrivedAtUpdateRequest deliveryInfoArrivedAtUpdateRequest);

    void deleteDeliveryInfo(Long id);

}
