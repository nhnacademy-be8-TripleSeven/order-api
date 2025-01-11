package com.tripleseven.orderapi.dto.order;

import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class OrderViewsResponseDTO {
    private Long orderId;

    private LocalDate orderDate;

    @Setter
    private String orderContent;

    @Setter
    private int price;

    @Setter
    private int amount;

    @Setter
    private OrderStatus orderStatus;

    private String ordererName;

    private String recipientName;

    public OrderViewsResponseDTO(Long orderId, LocalDate orderDate, String orderContent, int price, int amount,
                                 OrderStatus orderStatus, String ordererName, String recipientName) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderContent = orderContent;
        this.price = price;
        this.amount = amount;
        this.orderStatus = orderStatus;
        this.ordererName = ordererName;
        this.recipientName = recipientName;
    }

}
