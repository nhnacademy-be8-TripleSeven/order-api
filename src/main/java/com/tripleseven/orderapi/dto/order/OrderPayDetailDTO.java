package com.tripleseven.orderapi.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderPayDetailDTO {
    List<OrderInfoDTO> orderInfos;
    OrderGroupInfoDTO orderGroupInfoDTO;
    DeliveryInfoDTO deliveryInfo;
    OrderPayInfoDTO orderPayInfoDTO;


}
