package com.tripleseven.orderapi.service.pay;

import com.tripleseven.orderapi.dto.pay.Payment;
import com.tripleseven.orderapi.entity.pay.Pay;
import com.tripleseven.orderapi.repository.pay.PayRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {
    private final PayRepository payRepository;


    @Override
    public void save(JSONObject jsonObject) {
        Pay pay = new Pay();
        pay.ofCreate(jsonObject);
        payRepository.save(pay);
    }

    @Override
    public void payCancel(JSONObject response) {
        Pay pay = payRepository.findByPaymentKey(response.get("paymentKey").toString());
        if(Objects.isNull(pay)){
            throw new RuntimeException();
        }
        pay.ofUpdate(response);
        payRepository.save(pay);
    }
}
