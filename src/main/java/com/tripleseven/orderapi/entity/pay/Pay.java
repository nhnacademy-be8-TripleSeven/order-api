package com.tripleseven.orderapi.entity.pay;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Pay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //orderGroup 엔터티의 키
    private Long orderGroupId;

    private Long payTypeId;

    private OffsetDateTime requestedAt;

    private Long price;

    private String status; //결제 상태

    private String paymentKey;  //결제의 키 값, 결제 데이터 관리를 위해 반드시 저장해야함

    
    private String orderId;


    public void ofCreate(JSONObject response){
        orderId = response.get("orderId").toString();
        requestedAt = OffsetDateTime.parse(response.get("approvedAt").toString());
        price = Long.valueOf(response.get("balanceAmount").toString());
        status = response.get("status").toString();
        paymentKey = response.get("paymentKey").toString();
    }

    public void ofUpdate(JSONObject response){
        orderId = response.get("orderId").toString();
        requestedAt = OffsetDateTime.parse(response.get("approvedAt").toString());
        price = Long.valueOf(response.get("balanceAmount").toString());
        status = response.get("status").toString();
        paymentKey = response.get("paymentKey").toString();
    }
}
