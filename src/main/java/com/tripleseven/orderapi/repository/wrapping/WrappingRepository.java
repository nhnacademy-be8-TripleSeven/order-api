package com.tripleseven.orderapi.repository.wrapping;

import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WrappingRepository extends JpaRepository<Wrapping, Long> {
    Wrapping findWrappingById(Long id);
}
