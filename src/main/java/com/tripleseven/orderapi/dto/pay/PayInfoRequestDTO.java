package com.tripleseven.orderapi.dto.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayInfoRequestDTO {


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

    private String roadAddress;     // 도로명 주소
    private String zoneAddress;     // 지번 주소
    private String detailAddress;   // 상세 주소

    private LocalDate deliveryDate;//배송 날짜

    private long wrapperId; //포장지 아이디
    private long couponId; //쿠폰 아이디

    private long point; //사용하는 포인트
    private long totalAmount; //총 가격
}
