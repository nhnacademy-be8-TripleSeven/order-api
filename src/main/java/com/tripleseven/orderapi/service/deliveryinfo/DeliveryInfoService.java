package com.tripleseven.orderapi.service.deliveryinfo;

import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoUpdateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoCreateRequestDTO;
import com.tripleseven.orderapi.dto.deliveryinfo.DeliveryInfoResponseDTO;
import com.tripleseven.orderapi.dto.order.DeliveryInfoDTO;

public interface DeliveryInfoService {

    DeliveryInfoResponseDTO getDeliveryInfoById(Long id);

    DeliveryInfoResponseDTO createDeliveryInfo(DeliveryInfoCreateRequestDTO deliveryInfoCreateRequestDTO);

    DeliveryInfoResponseDTO updateDeliveryInfo(Long id, DeliveryInfoUpdateRequestDTO deliveryInfoUpdateRequestDTO);

    DeliveryInfoResponseDTO updateDeliveryInfoShippingAt(Long id);

    void deleteDeliveryInfo(Long id);

    // front 용
    DeliveryInfoDTO getDeliveryInfoDTO(Long orderGroupId);
}
