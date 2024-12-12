package com.example.orderapi.dto.pointhistory;

import com.example.orderapi.entity.PointHistory.HistoryTypes;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class PointHistoryCreateRequest {
    Long memberId;

    private HistoryTypes types;

    private int amount;

    private LocalDateTime changed_at;

    private String comment;
}
