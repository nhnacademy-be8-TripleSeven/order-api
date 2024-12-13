package com.example.orderapi.repository.wrapping;

import com.example.orderapi.entity.wrapping.Wrapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WrappingRepository extends JpaRepository<Wrapping, Long> {
}
