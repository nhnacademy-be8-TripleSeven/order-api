package com.tripleseven.orderapi.entity.defaultpointpolicy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PointPolicyType {
    NO_CONTENT_REVIEW("사진없는 리뷰"),
    CONTENT_REVIEW("사진 리뷰"),
    DEFAULT_BUY("기본 구매 적립"),
    REGISTER("회원가입"),
    ERROR("오류");

    private final String korean;

    PointPolicyType(String korean) {
        this.korean = korean;
    }

    @JsonCreator
    public static PointPolicyType fromString(String str) {
        for (PointPolicyType pointPolicyType : PointPolicyType.values()) {
            if (pointPolicyType.name().equalsIgnoreCase(str)) {
                return pointPolicyType;
            }
        }
        return ERROR;
    }

    @JsonValue
    public String toJson() {
        return this.name().toLowerCase();
    }
}
