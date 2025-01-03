package com.tripleseven.orderapi.repository.deliveryinfo;

import com.tripleseven.orderapi.dto.order.DeliveryInfoDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DeliveryInfoRepository extends JpaRepository<DeliveryInfo, Long> {
}
