package com.example.orderapi.entity.pointhistory;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter @Setter
@Data
@AllArgsConstructor
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private HistoryTypes types;

    //결제시 도서구매금액, 결제시 포인트 사용 금액
    private int amount;

    private LocalDateTime changed_at;

    private String comment;

    private Long memberId;

}
