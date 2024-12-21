package com.tripleseven.orderapi.service.pay;


import com.tripleseven.orderapi.dto.pay.Payment;

public interface PayService {
    void save(Payment payment);
}