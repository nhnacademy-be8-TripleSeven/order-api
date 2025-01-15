package com.tripleseven.orderapi.dto.order;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AddressInfoDTO implements Serializable {
    private String roadAddress;     // 도로명 주소
    private String zoneAddress;     // 지번 주소
    private String detailAddress;   // 상세 주소
}
