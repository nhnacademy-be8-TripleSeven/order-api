package com.tripleseven.orderapi.dto.paytypes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PayTypeCreateRequestDTO {
    private String name;  // 결제 유형 이름
}