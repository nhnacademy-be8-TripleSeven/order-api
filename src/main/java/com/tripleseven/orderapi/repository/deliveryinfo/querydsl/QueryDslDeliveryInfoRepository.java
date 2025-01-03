package com.tripleseven.orderapi.repository.deliveryinfo.querydsl;

import com.tripleseven.orderapi.dto.order.DeliveryInfoDTO;

public interface QueryDslDeliveryInfoRepository {
    DeliveryInfoDTO getDeliveryInfo(Long orderGroupId);
}
