package com.tripleseven.orderapi.repository.paytypes;

import com.tripleseven.orderapi.entity.paytypes.PayTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayTypesRepository extends JpaRepository<PayTypes, Long> {
}
