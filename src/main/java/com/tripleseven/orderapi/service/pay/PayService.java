package com.tripleseven.orderapi.service.pay;


import org.json.simple.JSONObject;

public interface PayService {
    void save(JSONObject response);
    void payCancel(JSONObject response);
}