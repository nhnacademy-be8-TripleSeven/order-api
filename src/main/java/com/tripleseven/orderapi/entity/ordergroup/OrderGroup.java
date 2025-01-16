package com.tripleseven.orderapi.entity.ordergroup;

import com.tripleseven.orderapi.entity.ordergrouppointhistory.OrderGroupPointHistory;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class OrderGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 비회원은 NULL
    private Long userId;

    @NotNull
    private String orderedName;

    @NotNull
    private LocalDate orderedAt;

    @NotNull
    private String recipientName;

    @NotNull
    private String recipientPhone;

    private String recipientHomePhone;

    @NotNull
    private long deliveryPrice;

    @NotNull
    private String address;

    @ManyToOne
    @JoinColumn(name = "wrapping_id")
    private Wrapping wrapping;

    @OneToMany(mappedBy = "orderGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderGroupPointHistory> orderGroupPointHistories = new ArrayList<>();


    public void ofCreate(Long userId, String orderedName, String recipientName, String recipientPhone, String recipientHomePhone, long deliveryPrice, String address, Wrapping wrapping) {
        this.userId = userId;
        this.orderedName = orderedName;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.deliveryPrice = deliveryPrice;
        this.recipientHomePhone = recipientHomePhone;
        this.address = address;
        this.orderedAt = LocalDate.now();
        this.wrapping = wrapping;
    }

    public void ofUpdate(String address) {
        this.address = address;
    }

    public void addOrderGroupPointHistory(OrderGroupPointHistory history) {
        this.orderGroupPointHistories.add(history);
    }
}
