package com.tripleseven.orderapi.repository.pay;

import com.tripleseven.orderapi.entity.pay.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRepository extends JpaRepository<Pay, Long> {

}
