package com.tripleseven.orderapi.repository.orderdetail.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.jpa.impl.JPAQuery;
import com.tripleseven.orderapi.dto.order.OrderViewDTO;
import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.OrderStatus;
import com.tripleseven.orderapi.entity.orderdetail.QOrderDetail;
import com.tripleseven.orderapi.entity.ordergroup.QOrderGroup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
public class QueryDslOrderDetailRepositoryImpl extends QuerydslRepositorySupport implements QueryDslOrderDetailRepository {
    public QueryDslOrderDetailRepositoryImpl() {
        super(OrderDetail.class);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<OrderViewDTO> findAllByPeriodAndUserId(Long userId, LocalDate startTime, LocalDate endTime, OrderStatus orderStatus) {

        QOrderGroup orderGroup = QOrderGroup.orderGroup;
        QOrderDetail orderDetail = QOrderDetail.orderDetail;
        List<OrderStatus> orderStatuses;

        // 주문 상태별 분기
        if (Objects.isNull(orderStatus)) {
            orderStatuses = OrderStatus.mainOrderStatuses();
        } else {
            orderStatuses = List.of(orderStatus);
        }

        return new JPAQuery<>(entityManager).select(Projections.constructor(OrderViewDTO.class,
                        orderGroup.id.as("orderId"),
                        orderGroup.orderedAt.as("orderDate"),
                        orderDetail.bookId.as("bookId"),
                        orderDetail.discountPrice.as("price"),
                        orderDetail.amount.as("amount"),
                        orderDetail.orderStatus.as("status"),
                        orderGroup.orderedName.as("ordererName"),
                        orderGroup.recipientName.as("recipientName")))
                .from(orderDetail)
                .join(orderDetail.orderGroup, orderGroup)
                .where(
                        orderGroup.userId.eq(userId)
                                .and(betweenDates(orderGroup.orderedAt, startTime, endTime))
                                .and(orderDetail.orderStatus.in(orderStatuses))
                )
                .orderBy(orderGroup.orderedAt.desc())
                .orderBy(orderDetail.id.desc())
                .fetch();
    }

    @Override
    public List<OrderViewDTO> findAllByPeriod(LocalDate startTime, LocalDate endTime, OrderStatus orderStatus) {

        QOrderGroup orderGroup = QOrderGroup.orderGroup;
        QOrderDetail orderDetail = QOrderDetail.orderDetail;
        List<OrderStatus> orderStatuses;

        // 주문 상태별 분기
        if (Objects.isNull(orderStatus)) {
            orderStatuses = OrderStatus.mainOrderStatuses();
        } else {
            orderStatuses = List.of(orderStatus);
        }

        return new JPAQuery<>(entityManager).select(Projections.constructor(OrderViewDTO.class,
                        orderGroup.id.as("orderId"),
                        orderGroup.orderedAt.as("orderDate"),
                        orderDetail.bookId.as("bookId"),
                        orderDetail.discountPrice.as("price"),
                        orderDetail.amount.as("amount"),
                        orderDetail.orderStatus.as("status"),
                        orderGroup.orderedName.as("ordererName"),
                        orderGroup.recipientName.as("recipientName")))
                .from(orderDetail)
                .join(orderDetail.orderGroup, orderGroup)
                .where(
                        betweenDates(orderGroup.orderedAt, startTime, endTime)
                                .and(orderDetail.orderStatus.in(orderStatuses))
                )
                .orderBy(orderGroup.orderedAt.desc())
                .orderBy(orderDetail.id.desc())
                .fetch();
    }

    private BooleanExpression betweenDates(
            DatePath<LocalDate> dateTimeField,
            LocalDate startTime,
            LocalDate endTime) {
        return dateTimeField.between(startTime, endTime);
    }


}
