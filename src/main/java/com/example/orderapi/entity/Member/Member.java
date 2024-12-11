package com.example.orderapi.entity.Member;

import com.example.orderapi.entity.PointHistory.PointHistory;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @OneToMany(mappedBy = "member")
    List<PointHistory> pointHistoryList;
}
