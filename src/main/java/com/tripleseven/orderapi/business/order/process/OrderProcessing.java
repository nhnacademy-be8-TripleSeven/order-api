package com.tripleseven.orderapi.business.order.process;

import com.tripleseven.orderapi.dto.pay.PaymentDTO;

public interface OrderProcessing {
    void processNonMemberOrder(String guestId, PaymentDTO paymentDTO);

    void processMemberOrder(Long memberId, PaymentDTO paymentDTO);
}
