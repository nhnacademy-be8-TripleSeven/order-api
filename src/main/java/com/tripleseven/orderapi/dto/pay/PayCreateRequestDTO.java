package com.tripleseven.orderapi.dto.pay;

import lombok.Getter;

@Getter
public class PayCreateRequestDTO {

    private String orderId;
    private int amount;
    private String orderName;
    private String customerName;
    private String customerEmail;

}
