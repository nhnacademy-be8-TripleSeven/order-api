package com.tripleseven.orderapi.entity.orderdetail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    PAYMENT_PENDING("결제대기"),
    PAYMENT_COMPLETED("결제완료"),
    SHIPPING("배송중"),
    DELIVERED("배송완료"),
    RETURNED("반품"),
    ORDER_CANCELED("주문취소"),
    ERROR("오류");

    private final String korean;

    Status(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }

    @JsonCreator
    public static Status fromString(String str) {
        for (Status status : Status.values()) {
            if (status.name().equalsIgnoreCase(str)) {
                return status;
            }
        }
        return ERROR;
    }

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }
}
