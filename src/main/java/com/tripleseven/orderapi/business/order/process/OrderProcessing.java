package com.tripleseven.orderapi.business.order.process;

public interface OrderProcessing {
    void processNonMemberOrder(String guestId);

    void processMemberOrder(Long memberId);
}
