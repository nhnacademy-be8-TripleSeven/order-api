package com.tripleseven.orderapi.entity.orderdetail;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Getter
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long bookId;

    @Min(1)
    @NotNull
    private int amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    private int primePrice;

    @NotNull
    private int discountPrice;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_group_id")
    private OrderGroup orderGroup;

    public void ofCreate(Long bookId, int amount, int primePrice, int discountPrice, OrderGroup orderGroup){
        this.bookId = bookId;
        this.amount = amount;
        this.primePrice = primePrice;
        this.discountPrice = discountPrice;
        this.orderGroup = orderGroup;
        this.status = Status.PAYMENT_PENDING;
    }

    public void ofUpdateStatus(Status status){
        this.status = status;
    }
}
