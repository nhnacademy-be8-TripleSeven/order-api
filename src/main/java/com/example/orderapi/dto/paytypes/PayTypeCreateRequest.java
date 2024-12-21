package com.example.orderapi.dto.paytypes;

import lombok.Getter;

@Getter
public class PayTypeCreateRequest {
    private String name;  // 결제 유형 이름
}