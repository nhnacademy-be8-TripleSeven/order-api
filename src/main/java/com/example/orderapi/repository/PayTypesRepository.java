package com.example.orderapi.repository;

import com.example.orderapi.entity.PayTypes.PayTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayTypesRepository extends JpaRepository<PayTypes, Long> {
}
