package com.tripleseven.orderapi.entity.ordergrouppointhistory;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class OrderGroupPointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "point_history_id", nullable = false)
    private PointHistory pointHistory;

    @ManyToOne
    @JoinColumn(name = "order_group_id", nullable = false)
    private OrderGroup orderGroup;


    public void ofCreate(PointHistory pointHistory, OrderGroup orderGroup) {
        this.pointHistory = pointHistory;
        this.orderGroup = orderGroup;

        orderGroup.addOrderGroupPointHistory(this);
        pointHistory.addOrderGroupPointHistory(this);

    }
}
