package com.tripleseven.orderapi.entity.orderdetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.List;

@Getter
public enum OrderStatus {
    PAYMENT_PENDING("결제대기"),
    PAYMENT_COMPLETED("결제완료"),
    SHIPPING("배송중"),
    DELIVERED("배송완료"),
    RETURNED_PENDING("반품대기"),
    RETURNED("반품"),
    ORDER_CANCELED("주문취소"),
    ERROR("오류");

    private final String korean;

    OrderStatus(String korean) {
        this.korean = korean;
    }

    @JsonCreator
    public static OrderStatus fromString(String str) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.name().equalsIgnoreCase(str)) {
                return orderStatus;
            }
        }
        return ERROR;
    }

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }

    public static List<OrderStatus> mainOrderStatuses() {
        return List.of(OrderStatus.ERROR, OrderStatus.PAYMENT_PENDING, OrderStatus.PAYMENT_COMPLETED, OrderStatus.SHIPPING, OrderStatus.DELIVERED);
    }
}
