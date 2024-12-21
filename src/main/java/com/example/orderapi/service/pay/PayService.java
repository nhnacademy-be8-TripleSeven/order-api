package com.example.orderapi.service.pay;


import com.example.orderapi.dto.pay.Payment;

public interface PayService {
    void save(Payment payment);
}