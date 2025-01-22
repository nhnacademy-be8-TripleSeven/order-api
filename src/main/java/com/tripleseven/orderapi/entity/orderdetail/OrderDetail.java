package com.tripleseven.orderapi.entity.orderdetail;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookId;

    @Min(1)
    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private long primePrice;

    @Column(nullable = false)
    private long discountPrice;

    @Column(nullable = false)
    private LocalDate updateDate;

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
        this.updateDate = LocalDate.now();
    }

    public void ofUpdateStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        this.updateDate = LocalDate.now();
    }

    public void ofZeroPrice() {
        this.primePrice = 0;
        this.discountPrice = 0;
    }

}
