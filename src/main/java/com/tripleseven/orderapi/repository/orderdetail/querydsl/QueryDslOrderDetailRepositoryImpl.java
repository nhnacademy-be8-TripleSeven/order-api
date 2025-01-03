package com.tripleseven.orderapi.repository.orderdetail.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.jpa.impl.JPAQuery;
import com.tripleseven.orderapi.dto.order.OrderViewDTO;
import com.tripleseven.orderapi.dto.order.OrderViewsRequestDTO;
import com.tripleseven.orderapi.entity.orderdetail.OrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.QOrderDetail;
import com.tripleseven.orderapi.entity.orderdetail.Status;
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
    public List<OrderViewDTO> findAllByPeriod(Long userId, LocalDate startTime, LocalDate endTime, Status status) {

        QOrderGroup orderGroup = QOrderGroup.orderGroup;
        QOrderDetail orderDetail = QOrderDetail.orderDetail;
        List<Status> statuses;

        // 주문 상태별 분기
        if (Objects.isNull(status)) {
            statuses = Arrays.asList(Status.ERROR, Status.PAYMENT_PENDING, Status.PAYMENT_COMPLETED, Status.SHIPPING, Status.DELIVERED);
        } else {
            statuses = List.of(status);
        }

        return new JPAQuery<>(entityManager).select(Projections.constructor(OrderViewDTO.class,
                        orderGroup.id.as("orderId"),
                        orderGroup.orderedAt.as("orderDate"),
                        orderDetail.bookId.as("bookId"),
                        orderDetail.discountPrice.as("price"),
                        orderDetail.amount.as("amount"),
                        orderDetail.status.as("status"),
                        orderGroup.orderedName.as("ordererName"),
                        orderGroup.recipientName.as("recipientName")))
                .from(orderDetail)
                .join(orderDetail.orderGroup, orderGroup)
                .where(
                        orderGroup.userId.eq(userId)
                                .and(betweenDates(orderGroup.orderedAt, startTime, endTime))
                                .and(orderDetail.status.in(statuses))
                )
                .orderBy(orderDetail.id.asc())
                .fetch();
    }

    private BooleanExpression betweenDates(
            DatePath<LocalDate> dateTimeField,
            LocalDate startTime,
            LocalDate endTime) {
        return dateTimeField.between(startTime, endTime);
    }
}
