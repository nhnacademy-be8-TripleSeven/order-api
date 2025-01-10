package com.tripleseven.orderapi.repository.ordergroup;

import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderGroupRepository extends JpaRepository<OrderGroup, Long> {

    List<OrderGroup> findAllByUserIdOrderByIdAsc(Long userId, Pageable pageable);

    OrderGroup findOrderGroupById(Long id);
    List<OrderGroup> findAllByRecipientPhone(String phone);
}
