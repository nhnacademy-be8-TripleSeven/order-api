package com.example.orderapi.dto.orderdetail;

import com.example.orderapi.entity.orderdetail.OrderDetail;
import com.example.orderapi.entity.orderdetail.Status;
import com.example.orderapi.environmentutils.EnvironmentUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Getter
@Slf4j
public class OrderDetailResponse {
    private final Long id;

    private final Long bookId;

    private final int amount;

    private final Status status;

    private final int price;

    private final Long wrappingId;

    private final Long orderGroupId;

    @Builder
    private OrderDetailResponse(Long id, Long bookId, int amount, Status status, int price, Long wrappingId, Long orderGroupId) {
        if (!EnvironmentUtil.isTestEnvironment() && Objects.isNull(id)) {
            log.error("OrderDetail id cannot be null");
            throw new IllegalArgumentException("id or bookId cannot be null");
        }
        this.id = id;
        this.bookId = bookId;
        this.amount = amount;
        this.status = status;
        this.price = price;
        this.wrappingId = wrappingId;
        this.orderGroupId = orderGroupId;
    }

    public static OrderDetailResponse fromEntity(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .bookId(orderDetail.getBookId())
                .amount(orderDetail.getAmount())
                .status(orderDetail.getStatus())
                .price(orderDetail.getPrice())
                .wrappingId(orderDetail.getWrapping().getId())
                .orderGroupId(orderDetail.getOrderGroup().getId())
                .build();

    }
}
