package com.tripleseven.orderapi.repository.defaultdeliverypolicy.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.tripleseven.orderapi.dto.defaultdeliverypolicy.DefaultDeliveryPolicyDTO;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.DefaultDeliveryPolicy;
import com.tripleseven.orderapi.entity.defaultdeliverypolicy.QDefaultDeliveryPolicy;
import com.tripleseven.orderapi.entity.deliverypolicy.QDeliveryPolicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QueryDslDefaultDeliveryPolicyRepositoryImpl extends QuerydslRepositorySupport implements QueryDslDefaultDeliveryPolicyRepository {
    public QueryDslDefaultDeliveryPolicyRepositoryImpl() {
        super(DefaultDeliveryPolicy.class);
    }

    @PersistenceContext
    private EntityManager entityManager;

    // 정책 타입 별 검색 (UNIQUE)
    @Override
    public List<DefaultDeliveryPolicyDTO> findDefaultDeliveryPolicy() {
        QDeliveryPolicy deliveryPolicy = QDeliveryPolicy.deliveryPolicy;
        QDefaultDeliveryPolicy defaultDeliveryPolicy = QDefaultDeliveryPolicy.defaultDeliveryPolicy;

        return new JPAQuery<>(entityManager)
                .select(Projections.constructor(DefaultDeliveryPolicyDTO.class,
                        deliveryPolicy.id.as("id"),
                        deliveryPolicy.name.as("name"),
                        deliveryPolicy.minPrice.as("minPrice"),
                        deliveryPolicy.price.as("price"),
                        defaultDeliveryPolicy.deliveryPolicyType.as("type")))
                .from(defaultDeliveryPolicy)
                .join(defaultDeliveryPolicy.deliveryPolicy, deliveryPolicy)
                .fetch();
    }
}
