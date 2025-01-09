package com.tripleseven.orderapi.entity.pointhistory;

public enum HistoryTypes {
    EARN("적립"),
    SPEND("사용");

    private final String koreanName;

    HistoryTypes(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}