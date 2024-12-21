package com.tripleseven.orderapi.repository.deliveryinfo;

import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryInfoRepository extends JpaRepository<DeliveryInfo, Long> {
}
