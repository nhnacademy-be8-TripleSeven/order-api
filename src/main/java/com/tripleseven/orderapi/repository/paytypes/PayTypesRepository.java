package com.tripleseven.orderapi.repository.paytypes;

import com.tripleseven.orderapi.entity.paytype.PayType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayTypesRepository extends JpaRepository<PayType, Long> {
    PayType findByName(String name);
}
