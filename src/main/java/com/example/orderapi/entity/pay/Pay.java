package com.example.orderapi.entity.pay;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Pay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderGroupId;

    private Long payTypeId;

    private Date date;

    private Long price;

    private boolean status; //결제 상태

    private String paymentKey;  //결제의 키 값, 결제 데이터 관리를 위해 반드시 저장해야함




}
