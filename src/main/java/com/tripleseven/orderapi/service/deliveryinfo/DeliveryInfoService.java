package com.tripleseven.orderapi.service.deliveryinfo;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoUpdateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponseDTO;

public interface DeliveryInfoService {

    DeliveryInfoResponseDTO getDeliveryInfoById(Long id);

    DeliveryInfoResponseDTO createDeliveryInfo(DeliveryInfoCreateRequestDTO deliveryInfoCreateRequestDTO);

    DeliveryInfoResponseDTO updateDeliveryInfo(Long id, DeliveryInfoUpdateRequestDTO deliveryInfoUpdateRequestDTO);

    DeliveryInfoResponseDTO updateDeliveryInfoShippingAt(Long id);

    void deleteDeliveryInfo(Long id);

}
