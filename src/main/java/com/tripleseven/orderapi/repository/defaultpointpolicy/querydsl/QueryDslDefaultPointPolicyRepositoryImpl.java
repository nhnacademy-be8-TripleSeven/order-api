package com.tripleseven.orderapi.repository.defaultpointpolicy.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.entity.defaultpointpolicy.DefaultPointPolicy;
import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import com.tripleseven.orderapi.entity.defaultpointpolicy.QDefaultPointPolicy;
import com.tripleseven.orderapi.entity.pointpolicy.QPointPolicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QueryDslDefaultPointPolicyRepositoryImpl extends QuerydslRepositorySupport implements QueryDslDefaultPointPolicyRepository {
    public QueryDslDefaultPointPolicyRepositoryImpl() {
        super(DefaultPointPolicy.class);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DefaultPointPolicyDTO findDefaultPointPolicyByType(PointPolicyType pointPolicyType) {
        QPointPolicy pointPolicy = QPointPolicy.pointPolicy;
        QDefaultPointPolicy defaultPointPolicy = QDefaultPointPolicy.defaultPointPolicy;

        return new JPAQuery<>(entityManager)
                .select(Projections.constructor(DefaultPointPolicyDTO.class,
                        defaultPointPolicy.id.as("id"),
                        defaultPointPolicy.pointPolicyType.as("type"),
                        pointPolicy.id.as("pointPolicyId"),
                        pointPolicy.name.as("name"),
                        pointPolicy.amount.as("amount"),
                        pointPolicy.rate.as("rate")))
                .from(defaultPointPolicy)
                .join(defaultPointPolicy.pointPolicy, pointPolicy)
                .where(
                        defaultPointPolicy.pointPolicyType.eq(pointPolicyType)
                )
                .fetchOne();
    }

    @Override
    public List<DefaultPointPolicyDTO> findDefaultPointPolicies() {
        QPointPolicy pointPolicy = QPointPolicy.pointPolicy;
        QDefaultPointPolicy defaultPointPolicy = QDefaultPointPolicy.defaultPointPolicy;

        return new JPAQuery<>(entityManager)
                .select(Projections.constructor(DefaultPointPolicyDTO.class,
                        defaultPointPolicy.id.as("id"),
                        defaultPointPolicy.pointPolicyType.as("type"),
                        pointPolicy.id.as("pointPolicyId"),
                        pointPolicy.name.as("name"),
                        pointPolicy.amount.as("amount"),
                        pointPolicy.rate.as("rate")))
                .from(defaultPointPolicy)
                .join(defaultPointPolicy.pointPolicy, pointPolicy)
                .fetch();
    }
}

