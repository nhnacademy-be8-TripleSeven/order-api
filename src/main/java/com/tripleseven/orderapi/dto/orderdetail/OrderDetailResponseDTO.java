package com.tripleseven.orderapi.dto.orderdetail;

import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Getter
@Slf4j
public class OrderDetailResponseDTO {
    private final Long id;

    private final Long bookId;

    private final int amount;

    private final Status status;

    private final int primePrice;

    private final int discountPrice;

    private final Long orderGroupId;

    @Builder
    private OrderDetailResponseDTO(Long id, Long bookId, int amount, Status status, int primePrice, int discountPrice, Long orderGroupId) {
        if (Objects.isNull(id)) {
            log.error("OrderDetail id cannot be null");
            throw new IllegalArgumentException("id or bookId cannot be null");
        }
        this.id = id;
        this.bookId = bookId;
        this.amount = amount;
        this.status = status;
        this.primePrice = primePrice;
        this.discountPrice = discountPrice;
        this.orderGroupId = orderGroupId;
    }

    public static OrderDetailResponseDTO fromEntity(OrderDetail orderDetail) {
        return OrderDetailResponseDTO.builder()
                .id(orderDetail.getId())
                .bookId(orderDetail.getBookId())
                .amount(orderDetail.getAmount())
                .status(orderDetail.getStatus())
                .primePrice(orderDetail.getPrimePrice())
                .discountPrice(orderDetail.getDiscountPrice())
                .orderGroupId(orderDetail.getOrderGroup().getId())
                .build();

    }
}
