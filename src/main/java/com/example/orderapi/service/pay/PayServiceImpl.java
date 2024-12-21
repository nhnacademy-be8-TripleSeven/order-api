package com.example.orderapi.service.pay;

import com.example.orderapi.dto.pay.Payment;
import com.example.orderapi.entity.pay.Pay;
import com.example.orderapi.repository.pay.PayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {
    private final PayRepository payRepository;


    @Override
    public void save(Payment payment) {
        Pay pay = new Pay();
        pay.ofCreate(payment);
        payRepository.save(pay);
    }
}
