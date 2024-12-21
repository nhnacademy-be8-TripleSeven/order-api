package com.example.orderapi.repository.orderdetail;

import com.example.orderapi.entity.orderdetail.OrderDetail;
import com.example.orderapi.entity.orderdetail.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAllByOrderGroupId(Long orderGroupId);
    List<OrderDetail> findAllByOrderGroupIdAndStatus(Long orderGroupId, Status status);
}
