package com.tripleseven.orderapi.dto.pointhistory;

import com.querydsl.core.annotations.QueryProjection;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserPointHistoryDTO {
    private Long pointHistoryId;
    private int amount;
    private LocalDateTime changedAt;
    private HistoryTypes types; // 영어 이름
    private String typesKoreanName; // 한글 이름
    private String comment;
    private Long orderGroupId;

    @QueryProjection
    public UserPointHistoryDTO(Long pointHistoryId, int amount, LocalDateTime changedAt, HistoryTypes types, String comment, Long orderGroupId) {
        this.pointHistoryId = pointHistoryId;
        this.amount = amount;
        this.changedAt = changedAt;
        this.types = types;
        this.typesKoreanName = types.getKoreanName(); // 한글 이름 추가
        this.comment = comment;
        this.orderGroupId = orderGroupId;
    }
}
