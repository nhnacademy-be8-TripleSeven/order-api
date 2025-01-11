package com.tripleseven.orderapi.entity.pointhistory;

import com.tripleseven.orderapi.entity.ordergrouppointhistory.OrderGroupPointHistory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "pointHistory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderGroupPointHistory> orderGroupPointHistories = new ArrayList<>();

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

    public void addOrderGroupPointHistory(OrderGroupPointHistory history) {
        this.orderGroupPointHistories.add(history);
    }
}