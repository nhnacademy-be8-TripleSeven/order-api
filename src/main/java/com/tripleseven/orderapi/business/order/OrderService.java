package com.tripleseven.orderapi.business.order;

import com.tripleseven.orderapi.dto.order.*;

import java.util.List;

public interface OrderService {
    List<OrderInfoDTO> getOrderInfos(Long orderGroupId);

    OrderGroupInfoDTO getOrderGroupInfo(Long orderGroupId);

    DeliveryInfoDTO getDeliveryInfo(Long orderGroupId);

    OrderPayInfoDTO getOrderPayInfo(Long orderGroupId);

    OrderPayDetailDTO getOrderPayDetail(Long orderGroupId);
}
