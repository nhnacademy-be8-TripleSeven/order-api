package com.example.orderapi.service.pay;

import com.example.orderapi.repository.pay.PayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {
    private final PayRepository payRepository;


}
