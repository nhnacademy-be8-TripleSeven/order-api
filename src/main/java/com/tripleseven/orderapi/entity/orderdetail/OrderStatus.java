package com.tripleseven.orderapi.entity.orderdetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum OrderStatus {
    ERROR("오류"),
    ORDER_CANCELED("주문취소"),
    PAYMENT_PENDING("결제대기"),
    PAYMENT_COMPLETED("결제완료"),
    SHIPPING("배송중"),
    DELIVERED("배송완료"),
    RETURNED_PENDING("반품대기"),
    RETURNED("반품");

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
}
