package com.example.orderapi.entity.PointHistory;

import com.example.orderapi.entity.Member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter @Setter
public class PointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private HistoryType types;

    private int amount;

    private LocalDateTime changed_at;

    private String comment;

    @ManyToOne
    Member member;
}
