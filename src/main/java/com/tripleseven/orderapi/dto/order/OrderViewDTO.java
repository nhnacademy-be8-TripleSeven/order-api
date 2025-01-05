package com.tripleseven.orderapi.dto.order;

import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class OrderViewDTO {
    private final Long orderId;
    private final LocalDate orderDate;
    private final Long bookId;
    private final int price;
    private final int amount;
    private final OrderStatus orderStatus;
    private final String ordererName;
    private final String recipientName;

    public OrderViewDTO(Long orderId, LocalDate orderDate, Long bookId, int price, int amount,
                        OrderStatus orderStatus, String ordererName, String recipientName) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.bookId = bookId;
        this.price = price;
        this.amount = amount;
        this.orderStatus = orderStatus;
        this.ordererName = ordererName;
        this.recipientName = recipientName;
    }
}
