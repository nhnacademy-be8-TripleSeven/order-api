package com.tripleseven.orderapi.repository.querydsl;

import com.tripleseven.orderapi.dto.defaultpointpolicy.DefaultPointPolicyDTO;
import com.tripleseven.orderapi.entity.defaultpointpolicy.DefaultPointPolicy;
import com.tripleseven.orderapi.entity.defaultpointpolicy.PointPolicyType;
import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.repository.defaultpointpolicy.DefaultPointPolicyRepository;
import com.tripleseven.orderapi.repository.defaultpointpolicy.querydsl.QueryDslDefaultPointPolicyRepositoryImpl;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Transactional
@Import(QueryDslDefaultPointPolicyRepositoryImpl.class)
class QueryDslDefaultPointPolicyTest {

    @Autowired
    private QueryDslDefaultPointPolicyRepositoryImpl queryDslDefaultPointPolicyRepository;

    @Autowired
    private DefaultPointPolicyRepository defaultPointPolicyRepository;

    @Autowired
    private PointPolicyRepository pointPolicyRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findDefaultPointPolicyByType_shouldReturnCorrectDTO() {
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("Test Point Policy", 100, BigDecimal.ZERO);
        PointPolicy savedPointPolicy = pointPolicyRepository.save(pointPolicy);

        DefaultPointPolicy defaultPointPolicy = new DefaultPointPolicy();
        defaultPointPolicy.ofCreate(PointPolicyType.DEFAULT_BUY, savedPointPolicy);
        defaultPointPolicyRepository.save(defaultPointPolicy);

        entityManager.flush();
        entityManager.clear();

        DefaultPointPolicyDTO result = queryDslDefaultPointPolicyRepository.findDefaultPointPolicyByType(PointPolicyType.DEFAULT_BUY);

        assertNotNull(result);
        assertEquals(PointPolicyType.DEFAULT_BUY, result.getType());
        assertEquals(savedPointPolicy.getId(), result.getPointPolicyId());
        assertEquals("Test Point Policy", result.getName());
        assertEquals(100, result.getAmount());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getRate()));
    }

    @Test
    void findDefaultPointPolicies_shouldReturnAllDTOs() {
        PointPolicy pointPolicy1 = new PointPolicy();
        pointPolicy1.ofCreate("Policy 1", 200, BigDecimal.ZERO);
        PointPolicy savedPolicy1 = pointPolicyRepository.save(pointPolicy1);

        PointPolicy pointPolicy2 = new PointPolicy();
        pointPolicy2.ofCreate("Policy 2", 300, BigDecimal.ZERO);
        PointPolicy savedPolicy2 = pointPolicyRepository.save(pointPolicy2);

        DefaultPointPolicy defaultPointPolicy1 = new DefaultPointPolicy();
        defaultPointPolicy1.ofCreate(PointPolicyType.DEFAULT_BUY, savedPolicy1);
        defaultPointPolicyRepository.save(defaultPointPolicy1);

        DefaultPointPolicy defaultPointPolicy2 = new DefaultPointPolicy();
        defaultPointPolicy2.ofCreate(PointPolicyType.CONTENT_REVIEW, savedPolicy2);
        defaultPointPolicyRepository.save(defaultPointPolicy2);

        entityManager.flush();
        entityManager.clear();

        List<DefaultPointPolicyDTO> result = queryDslDefaultPointPolicyRepository.findDefaultPointPolicies();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Policy 1", result.get(0).getName());
        assertEquals("Policy 2", result.get(1).getName());
    }
}

