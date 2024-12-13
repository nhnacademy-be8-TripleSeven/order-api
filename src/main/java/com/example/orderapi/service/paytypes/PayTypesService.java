package com.example.orderapi.service.paytypes;

import com.example.orderapi.entity.paytypes.PayTypes;

import java.util.List;

public interface PayTypesService {
    List<PayTypes> findAll();
    PayTypes save(PayTypes payTypes);
    PayTypes findById(Long id);
    void deleteById(Long id);
    void update(PayTypes payTypes);
}
