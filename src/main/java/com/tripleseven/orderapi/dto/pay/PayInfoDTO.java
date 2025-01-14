package com.tripleseven.orderapi.dto.pay;

import com.tripleseven.orderapi.dto.order.AddressInfoDTO;
import com.tripleseven.orderapi.dto.order.OrderBookInfoDTO;
import com.tripleseven.orderapi.dto.order.RecipientInfoDTO;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;


@Getter
public class PayInfoDTO {

    private Long orderId;

    private List<OrderBookInfoDTO> bookOrderDetails;
    // 받는 사람 정보
    private RecipientInfoDTO recipientInfo;

    private AddressInfoDTO addressInfo;

    private LocalDate deliveryDate;//배송 날짜

    private long wrapperId; //포장지 아이디
    private long couponId; //쿠폰 아이디

    private long point; //사용하는 포인트
    private long totalAmount; //총 가격

    public void ofCreate(Long orderId, PayInfoRequestDTO payInfoRequestDTO) {
        this.orderId = orderId;

        this.recipientInfo = payInfoRequestDTO.getRecipientInfo();
        this.addressInfo = payInfoRequestDTO.getAddressInfo();
        this.deliveryDate = payInfoRequestDTO.getDeliveryDate();
        this.wrapperId = payInfoRequestDTO.getWrapperId();
        this.couponId = payInfoRequestDTO.getCouponId();
        this.point = payInfoRequestDTO.getPoint();
        this.totalAmount = payInfoRequestDTO.getTotalAmount();
    }
}
