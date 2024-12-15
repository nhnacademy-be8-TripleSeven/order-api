package com.example.orderapi.dto.pointhistory;

import com.example.orderapi.entity.pointhistory.HistoryTypes;
import com.example.orderapi.entity.pointhistory.PointHistory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PointHistoryResponse {
    private Long id;

    @Enumerated(EnumType.STRING)
    private HistoryTypes types;

    private int amount;

    private LocalDateTime changed_at;

    private String comment;

    @Builder
    private PointHistoryResponse(Long id, HistoryTypes types, int amount, LocalDateTime changed_at, String comment) {
        this.id = id;
        this.types = types;
        this.amount = amount;
        this.changed_at = changed_at;
        this.comment = comment;
    }

    public static PointHistoryResponse fromEntity(PointHistory pointHistory) {
        return new PointHistoryResponse(pointHistory.getId(),
                pointHistory.getTypes(),
                pointHistory.getAmount(),
                pointHistory.getChanged_at(),
                pointHistory.getComment());
    }
}
