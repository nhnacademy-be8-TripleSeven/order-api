package com.tripleseven.orderapi.repository.orderdetail;

import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAllByOrderGroupId(Long orderGroupId);

    @Query("select case when exists (" +
            "select 1 " +
            "from OrderDetail od " +
            "join od.orderGroup og " +
            "where og.userId = :userId and od.bookId = :bookId) then true else false end")
    boolean existsByOrderGroupUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    List<OrderDetail> findByOrderStatusAndUpdateDateBefore(OrderStatus orderStatus, LocalDate updateDateBefore);
}
