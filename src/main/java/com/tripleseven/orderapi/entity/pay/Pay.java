package com.tripleseven.orderapi.entity.pay;

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

    private int price;

    private String status; //결제 상태

    private String paymentKey;  //결제의 키 값, 결제 데이터 관리를 위해 반드시 저장해야함

    @ManyToOne
    @JoinColumn(name = "pay_type_id")
    private PayType payType;

    @ManyToOne
    @JoinColumn(name = "order_group_id")
    private OrderGroup orderGroup;


    public void ofCreate(JSONObject response){
        orderId = Long.valueOf(response.get("orderId").toString());
        requestedAt = OffsetDateTime.parse(response.get("approvedAt").toString()).toLocalDate();
        price = Integer.parseInt(response.get("balanceAmount").toString());
        status = response.get("status").toString();
        paymentKey = response.get("paymentKey").toString();
    }

    public void ofUpdate(JSONObject response){
        orderId = Long.valueOf(response.get("orderId").toString());
        requestedAt = OffsetDateTime.parse(response.get("approvedAt").toString()).toLocalDate();
        price = Integer.parseInt(response.get("balanceAmount").toString());
        status = response.get("status").toString();
        paymentKey = response.get("paymentKey").toString();
    }


}
