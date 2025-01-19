package com.tripleseven.orderapi.service.pay;


import com.tripleseven.orderapi.dto.order.OrderPayInfoDTO;
import com.tripleseven.orderapi.dto.pay.PayCancelRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoRequestDTO;
import com.tripleseven.orderapi.dto.pay.PayInfoResponseDTO;
import com.tripleseven.orderapi.dto.pay.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface PayService {
    void createPay(PaymentDTO response, Long orderGroupId, String payType);

    Object cancelRequest(String paymentKey, PayCancelRequestDTO request) throws IOException;

    PayInfoResponseDTO createPayInfo(Long userId, String guestId, PayInfoRequestDTO requestDTO);

    OrderPayInfoDTO getOrderPayInfo(Long orderId);

    Object confirmRequest(HttpServletRequest request, String jsonBody) throws IOException;

    Object getPaymentInfo(String paymentKey) throws IOException;

    Long getOrderId(Long orderId);
}