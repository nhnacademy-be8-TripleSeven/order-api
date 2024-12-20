package com.tripleseven.orderapi.entity.pointhistory;

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

    private int amount;

    private LocalDateTime changed_at;

    private String comment;

    private Long memberId;
}
