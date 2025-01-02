package com.tripleseven.orderapi.dto.order;

import com.tripleseven.orderapi.entity.orderdetail.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class OrderViewsRequestDTO {
    private final Long orderId;
    private final LocalDate orderDate;
    @Setter
    private String orderContent;
    @Setter
    private int price;
    @Setter
    private int amount;
    @Setter
    private Status status;
    private final String ordererName;
    private final String recipientName;

    public OrderViewsRequestDTO(Long orderId, LocalDate orderDate, String orderContent, int price, int amount,
                                Status status, String ordererName, String recipientName) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderContent = orderContent;
        this.price = price;
        this.amount = amount;
        this.status = status;
        this.ordererName = ordererName;
        this.recipientName = recipientName;
    }

}
