package com.tripleseven.orderapi.repository.pay;

import com.tripleseven.orderapi.dto.order.OrderPayInfoDTO;
import com.tripleseven.orderapi.entity.pay.Pay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PayRepository extends JpaRepository<Pay, Long> {

    Pay findByPaymentKey(String orderId);

    @Query("SELECT new com.tripleseven.orderapi.dto.order.OrderPayInfoDTO(p.price, p.paymentKey, pt.name, p.requestedAt) " +
            "FROM Pay p " +
            "JOIN p.payType pt " +
            "JOIN p.orderGroup og " +
            "WHERE p.orderGroup.id = :orderId")
    OrderPayInfoDTO getDTOByOrderGroupId(Long orderId);
}
