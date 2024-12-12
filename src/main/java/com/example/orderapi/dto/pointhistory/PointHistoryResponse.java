package com.example.orderapi.dto.pointhistory;

import com.example.orderapi.entity.PointHistory.HistoryTypes;
import com.example.orderapi.entity.PointHistory.PointHistory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PointHistoryResponse {
    private Long id;

    @Enumerated(EnumType.STRING)
    private HistoryTypes types;

    private int amount;

    private LocalDateTime changed_at;

    private String comment;

    public static PointHistoryResponse fromEntity(PointHistory pointHistory) {
        PointHistoryResponse pointHistoryResponse = new PointHistoryResponse();
        pointHistoryResponse.setId(pointHistory.getId());
        pointHistoryResponse.setTypes(pointHistory.getTypes());
        pointHistoryResponse.setAmount(pointHistory.getAmount());
        pointHistoryResponse.setChanged_at(pointHistory.getChanged_at());
        pointHistoryResponse.setComment(pointHistory.getComment());
        return pointHistoryResponse;
    }
}
