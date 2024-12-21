package com.tripleseven.orderapi.dto.pointhistory;

import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PointHistoryCreateRequest {
    Long memberId;

    private HistoryTypes types;

    private int amount;

    private LocalDateTime changed_at;

    private String comment;
}