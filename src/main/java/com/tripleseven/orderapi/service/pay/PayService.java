package com.tripleseven.orderapi.service.pay;


import com.tripleseven.orderapi.dto.order.OrderPayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoResponseDTO;
import org.json.simple.JSONObject;

public interface PayService {
    void createPay(Long userId, JSONObject response);

    void cancelPay(JSONObject response);

    PayInfoResponseDTO createPayInfo(Long userId, String guestId, PayInfoRequestDTO requestDTO);

    OrderPayInfoDTO getOrderPayInfo(Long orderId);
}