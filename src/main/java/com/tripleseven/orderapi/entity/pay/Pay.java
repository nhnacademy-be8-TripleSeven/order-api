package com.tripleseven.orderapi.entity.pay;

import com.tripleseven.orderapi.dto.pay.PaymentDTO;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.paytype.PayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Pay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private LocalDate requestedAt;

    private long price;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; //결제 상태

    private String paymentKey;  //결제의 키 값, 결제 데이터 관리를 위해 반드시 저장해야함

    @ManyToOne
    @JoinColumn(name = "pay_type_id")
    private PayType payType;

    @ManyToOne
    @JoinColumn(name = "order_group_id")
    private OrderGroup orderGroup;



    // 결제 정보 생성 메서드
    public void ofCreate(PaymentDTO response, OrderGroup orderGroup) {
        this.orderId = response.getOrderId();
        this.requestedAt = response.getRequestedAt();
        this.price = response.getBalanceAmount();
        this.status = PaymentStatus.valueOf(response.getStatus().name());
        this.paymentKey = response.getPaymentKey();
        this.orderGroup = orderGroup;
    }

    // 결제 정보 갱신 메서드
    public void ofUpdate(PaymentDTO response) {
        this.orderId = response.getOrderId();
        this.requestedAt = response.getRequestedAt();
        this.price = response.getBalanceAmount();
        this.status = PaymentStatus.valueOf(response.getStatus().name());
        this.paymentKey = response.getPaymentKey();
    }


}
