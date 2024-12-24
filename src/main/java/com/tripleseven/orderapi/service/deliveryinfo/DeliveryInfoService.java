package com.tripleseven.orderapi.service.deliveryinfo;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoArrivedAtUpdateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponseDTO;

public interface DeliveryInfoService {

    DeliveryInfoResponseDTO getDeliveryInfoById(Long id);

    DeliveryInfoResponseDTO createDeliveryInfo(DeliveryInfoCreateRequestDTO deliveryInfoCreateRequestDTO);

    DeliveryInfoResponseDTO updateDeliveryInfoArrivedAt(Long id, DeliveryInfoArrivedAtUpdateRequestDTO deliveryInfoArrivedAtUpdateRequestDTO);

    void deleteDeliveryInfo(Long id);

}
