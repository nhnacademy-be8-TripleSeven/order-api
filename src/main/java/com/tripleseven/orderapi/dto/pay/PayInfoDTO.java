package com.tripleseven.orderapi.dto.pay;

import com.tripleseven.orderapi.dto.order.AddressInfoDTO;
import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import com.tripleseven.orderapi.dto.order.RecipientInfoDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Getter
@EqualsAndHashCode
public class PayInfoDTO implements Serializable {

    private Long orderId;

    private List<OrderBookInfoDTO> bookOrderDetails;
    // 받는 사람 정보
    private RecipientInfoDTO recipientInfo;

    private AddressInfoDTO addressInfo;

    private LocalDate deliveryDate; //배송 날짜

    private long wrapperId; //포장지 아이디
    private long couponId; //쿠폰 아이디

    private String ordererName;


    private long point; //사용하는 포인트
    private long totalAmount; //총 가격

    public void ofCreate(Long orderId, PayInfoRequestDTO payInfoRequestDTO) {
        this.orderId = orderId;
        this.bookOrderDetails = payInfoRequestDTO.getBookOrderDetails();
        this.recipientInfo = payInfoRequestDTO.getRecipientInfo();
        this.addressInfo = payInfoRequestDTO.getAddressInfo();
        this.deliveryDate = payInfoRequestDTO.getDeliveryDate();
        this.wrapperId = payInfoRequestDTO.getWrapperId();
        this.couponId = payInfoRequestDTO.getCouponId();
        this.ordererName = payInfoRequestDTO.getOrdererName();
        this.point = payInfoRequestDTO.getPoint();
        this.totalAmount = payInfoRequestDTO.getTotalAmount();
    }
}
