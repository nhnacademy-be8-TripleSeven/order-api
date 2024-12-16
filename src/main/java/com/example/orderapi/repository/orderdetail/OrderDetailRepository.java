package com.example.orderapi.repository.orderdetail;

import com.example.orderapi.entity.orderdetail.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
