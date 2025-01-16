package com.tripleseven.orderapi.service.pay;


import com.tripleseven.orderapi.dto.order.OrderPayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PayCancelRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoResponseDTO;
import com.tripleseven.orderapi.dto.pay.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;

import java.io.IOException;

public interface PayService {
    void createPay(Long userId, JSONObject response);

    Object cancelRequest(String paymentKey, PayCancelRequestDTO request) throws IOException;

    PayInfoResponseDTO createPayInfo(Long userId, String guestId, PayInfoRequestDTO requestDTO);

    OrderPayInfoDTO getOrderPayInfo(Long orderId);

    Object confirmRequest(HttpServletRequest request, String jsonBody) throws IOException;

    PaymentDTO getPaymentInfo(String paymentKey);
}