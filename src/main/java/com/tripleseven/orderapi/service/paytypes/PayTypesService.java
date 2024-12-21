package com.tripleseven.orderapi.service.paytypes;

import com.tripleseven.orderapi.entity.paytypes.PayTypes;

import java.util.List;

public interface PayTypesService {
    List<PayTypes> findAll();
    PayTypes save(PayTypes payTypes);
    PayTypes findById(Long id);
    void deleteById(Long id);
    void update(PayTypes payTypes);
}
