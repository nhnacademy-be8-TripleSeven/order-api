package com.tripleseven.orderapi.entity.orderdetail;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
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

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @NotNull
    private long primePrice;

    @NotNull
    private long discountPrice;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_group_id")
    private OrderGroup orderGroup;

    public void ofCreate(Long bookId, int amount, long primePrice, long discountPrice, OrderGroup orderGroup) {
        this.bookId = bookId;
        this.amount = amount;
        this.primePrice = primePrice;
        this.discountPrice = discountPrice;
        this.orderGroup = orderGroup;
        this.orderStatus = OrderStatus.PAYMENT_PENDING;
    }

    public void ofUpdateStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void ofZeroPrice() {
        this.primePrice = 0;
        this.discountPrice = 0;
    }

}
