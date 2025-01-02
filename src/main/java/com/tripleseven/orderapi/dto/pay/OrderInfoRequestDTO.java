package com.tripleseven.orderapi.dto.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoRequestDTO {


    // 주문자 정보
    private String customerName;       // 주문자 이름
    private String customerPhone;      // 주문자 휴대폰 번호
    private String customerLandline;   // 주문자 일반 전화
    private String customerEmail;      // 주문자 이메일
    private String customerPassword;   // 주문자 비밀번호

    // 받는 사람 정보
    private String recipientName;     // 받는 사람 이름
    private String recipientPhone;    // 받는 사람 휴대폰 번호
    private String recipientLandline; // 받는 사람 일반 전화
    private String recipientAddress;  // 받는 사람 주소

    private long wrapperId;
    private long couponId;

    private long point;
    private long totalAmount;
}
