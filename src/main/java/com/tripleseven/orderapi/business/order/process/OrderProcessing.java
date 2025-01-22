package com.tripleseven.orderapi.business.order.process;

import com.tripleseven.orderapi.dto.pay.PaymentDTO;

public interface OrderProcessing {
    void processOrder(Long memberId, String guestId, PaymentDTO paymentDTO);
}
