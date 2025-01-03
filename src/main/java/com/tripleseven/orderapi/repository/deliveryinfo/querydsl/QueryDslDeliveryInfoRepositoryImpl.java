package com.tripleseven.orderapi.repository.deliveryinfo.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.tripleseven.orderapi.dto.order.DeliveryInfoDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.deliveryinfo.QDeliveryInfo;
import com.tripleseven.orderapi.entity.ordergroup.QOrderGroup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class QueryDslDeliveryInfoRepositoryImpl extends QuerydslRepositorySupport implements QueryDslDeliveryInfoRepository {
    public QueryDslDeliveryInfoRepositoryImpl() {
        super(DeliveryInfo.class);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DeliveryInfoDTO getDeliveryInfo(Long orderGroupId) {
        QOrderGroup orderGroup = QOrderGroup.orderGroup;
        QDeliveryInfo deliveryInfo = QDeliveryInfo.deliveryInfo;
        return new JPAQuery<>(entityManager).select(Projections.constructor(DeliveryInfoDTO.class,
                        deliveryInfo.name.as("deliveryInfoName"),
                        deliveryInfo.invoiceNumber.as("invoiceNumber"),
                        deliveryInfo.arrivedAt.as("arrivedAt"),
                        orderGroup.id.as("orderId"),
                        orderGroup.orderedAt.as("orderedAt"),
                        orderGroup.orderedName.as("ordererName"),
                        orderGroup.recipientName.as("recipientName"),
                        orderGroup.recipientPhone.as("recipientPhone"),
                        orderGroup.address.as("address")))
                .from(deliveryInfo)
                .join(deliveryInfo.orderGroup, orderGroup)
                .fetchOne();
    }
}
