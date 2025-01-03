package com.tripleseven.orderapi.entity.pointhistory;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
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

    @ManyToOne
    @JoinColumn(name = "order_group_id")
    private OrderGroup orderGroup;

    public PointHistory(HistoryTypes types, int amount, LocalDateTime changedAt, String comment, Long memberId, OrderGroup orderGroup) {
        this.types = types;
        this.amount = amount;
        this.changedAt = changedAt;
        this.comment = comment;
        this.memberId = memberId;
        this.orderGroup = orderGroup;
    }

    public static PointHistory ofCreate(HistoryTypes types, int amount, String comment, Long memberId, OrderGroup orderGroup) {
        return new PointHistory(types, amount, LocalDateTime.now(), comment, memberId, orderGroup);
    }

    public void ofUpdate(HistoryTypes types, int amount, String comment) {
        this.types = types;
        this.amount = amount;
        this.comment = comment;
        this.changedAt = LocalDateTime.now();
    }
}