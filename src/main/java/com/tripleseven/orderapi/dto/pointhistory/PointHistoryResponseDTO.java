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
public class PointHistoryResponseDTO {
    private final Long id;

    @Enumerated(EnumType.STRING)
    private final HistoryTypes types;

    private final int amount;

    private final LocalDateTime changedAt;

    private final String comment;

    @Builder
    public PointHistoryResponseDTO(Long id, HistoryTypes types, int amount, LocalDateTime changedAt, String comment) {
        // Validate input parameters using Objects.isNull()
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (Objects.isNull(types)) {
            throw new IllegalArgumentException("History Type cannot be null");
        }

        if (Objects.isNull(changedAt)) {
            throw new IllegalArgumentException("Changed At cannot be null");
        }
        if (Objects.isNull(comment) || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be null or empty");
        }

        this.id = id;
        this.types = types;
        this.amount = amount;
        this.changedAt = changedAt;
        this.comment = comment;
    }

    public static PointHistoryResponseDTO fromEntity(PointHistory pointHistory) {
        return new PointHistoryResponseDTO(
                pointHistory.getId(),
                pointHistory.getTypes(),
                pointHistory.getAmount(),
                pointHistory.getChangedAt(),
                pointHistory.getComment());
    }
}
