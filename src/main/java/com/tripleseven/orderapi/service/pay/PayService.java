package com.tripleseven.orderapi.service.pay;


import com.tripleseven.orderapi.dto.pay.Payment;
import org.json.simple.JSONObject;

public interface PayService {
    void save(JSONObject response);
    void payCancel(JSONObject response);
}