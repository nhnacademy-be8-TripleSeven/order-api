package com.example.orderapi.entity.pointhistory;

import com.example.orderapi.dto.pointhistory.PointHistoryCreateRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private HistoryTypes types;

    private int amount; // 도서 구매금액 또는 포인트 사용금액

    private LocalDateTime changedAt; // 포인트 변경 시간

    private String comment; // 포인트 사용 설명

    private Long memberId; // 회원 ID

    public PointHistory(HistoryTypes types, int amount, LocalDateTime changedAt, String comment, Long memberId) {
        this.types = types;
        this.amount = amount;
        this.changedAt = changedAt;
        this.comment = comment;
        this.memberId = memberId;
    }

    public static PointHistory ofCreate(HistoryTypes types, int amount, String comment, Long memberId) {
        return new PointHistory(types, amount, LocalDateTime.now(), comment, memberId);
    }

    public void ofUpdate(HistoryTypes types, int amount, String comment) {
        this.types = types;
        this.amount = amount;
        this.comment = comment;
        this.changedAt = LocalDateTime.now();
    }
}