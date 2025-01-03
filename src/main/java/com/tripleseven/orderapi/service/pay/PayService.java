package com.tripleseven.orderapi.service.pay;


import com.tripleseven.orderapi.dto.order.OrderPayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoResponseDTO;
import org.json.simple.JSONObject;

public interface PayService {
    void save(Long userId, JSONObject response);
    void payCancel(JSONObject response);
    PayInfoResponseDTO getOrderInfo(Long userId, PayInfoRequestDTO requestDTO);
    OrderPayInfoDTO getOrderPayInfo(Long orderId);
}