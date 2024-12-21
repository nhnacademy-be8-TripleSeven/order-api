package com.tripleseven.orderapi.dto.pointhistory;

import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class PointHistoryResponse {
    private final Long id;

    @Enumerated(EnumType.STRING)
    private final HistoryTypes types;

    private final int amount;

    private final LocalDateTime changed_at;

    private final String comment;

    @Builder
    public PointHistoryResponse(Long id, HistoryTypes types, int amount, LocalDateTime changed_at, String comment) {
        // Validate input parameters using Objects.isNull()
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (Objects.isNull(types)) {
            throw new IllegalArgumentException("History Type cannot be null");
        }

        if (Objects.isNull(changed_at)) {
            throw new IllegalArgumentException("Changed At cannot be null");
        }
        if (Objects.isNull(comment) || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be null or empty");
        }

        this.id = id;
        this.types = types;
        this.amount = amount;
        this.changed_at = changed_at;
        this.comment = comment;
    }

    public static PointHistoryResponse fromEntity(PointHistory pointHistory) {
        return new PointHistoryResponse(
                pointHistory.getId(),
                pointHistory.getTypes(),
                pointHistory.getAmount(),
                pointHistory.getChangedAt(),
                pointHistory.getComment());
    }
}
