package com.example.orderapi.repository.deliveryinfo;

import com.example.orderapi.entity.DeliveryInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryInfoRepository extends JpaRepository<DeliveryInfo, Long> {
}
