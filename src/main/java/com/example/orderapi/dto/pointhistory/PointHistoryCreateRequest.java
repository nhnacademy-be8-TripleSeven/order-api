package com.example.orderapi.dto.pointhistory;

import com.example.orderapi.entity.pointhistory.HistoryTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PointHistoryCreateRequest {
    Long memberId;

    private HistoryTypes types;

    private int amount;

    private LocalDateTime changed_at;

    private String comment;
}
