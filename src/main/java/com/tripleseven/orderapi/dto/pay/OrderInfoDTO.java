package com.tripleseven.orderapi.dto.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDTO {

    private Long orderId;
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

    public void ofCreate(Long orderId, OrderInfoRequestDTO orderInfoRequestDTO) {
        this.orderId = orderId;
        this.customerName = orderInfoRequestDTO.getCustomerName();
        this.customerPhone = orderInfoRequestDTO.getCustomerPhone();
        this.customerLandline = orderInfoRequestDTO.getCustomerLandline();
        this.customerEmail = orderInfoRequestDTO.getCustomerEmail();
        this.customerPassword = orderInfoRequestDTO.getCustomerPassword();
        this.recipientName = orderInfoRequestDTO.getRecipientName();
        this.recipientPhone = orderInfoRequestDTO.getRecipientPhone();
        this.recipientLandline = orderInfoRequestDTO.getRecipientLandline();
        this.recipientAddress = orderInfoRequestDTO.getRecipientAddress();
        this.wrapperId = orderInfoRequestDTO.getWrapperId();
        this.couponId = orderInfoRequestDTO.getCouponId();
        this.point = orderInfoRequestDTO.getPoint();
        this.totalAmount = orderInfoRequestDTO.getTotalAmount();
    }
}
