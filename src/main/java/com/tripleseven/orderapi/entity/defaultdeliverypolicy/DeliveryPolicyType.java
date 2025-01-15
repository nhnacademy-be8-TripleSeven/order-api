package com.tripleseven.orderapi.entity.defaultdeliverypolicy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DeliveryPolicyType {
    DEFAULT("기본 배송"),
    EVENT("이벤트 배송"),
    ERROR("오류");

    private final String korean;

    DeliveryPolicyType(String korean) {
        this.korean = korean;
    }
}
