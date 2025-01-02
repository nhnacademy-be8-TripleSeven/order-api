package com.tripleseven.orderapi.repository.orderdetail;

import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAllByOrderGroupId(Long orderGroupId);
    List<OrderDetail> findAllByOrderGroupIdAndStatus(Long orderGroupId, Status status);
    @Query("select case when count(od) > 0 then true else false end " +
            "from OrderDetail od " +
            "join od.orderGroup og " +
            "where og.userId = :userId and od.bookId = :bookId")
    boolean existsByOrderGroupUserIdAndBookId(@Param("userId")Long userId, @Param("bookId") Long bookId);
}
