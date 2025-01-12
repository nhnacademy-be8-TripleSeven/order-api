package com.tripleseven.orderapi.repository;

import com.tripleseven.orderapi.entity.pointpolicy.PointPolicy;
import com.tripleseven.orderapi.repository.pointpolicy.PointPolicyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PointPolicyRepositoryTest {

    @Autowired
    private PointPolicyRepository pointPolicyRepository;

    @Test
    void savePointPolicy_success() {
        // given
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("Welcome Bonus", 500, BigDecimal.valueOf(0.05));

        // when
        PointPolicy savedPolicy = pointPolicyRepository.save(pointPolicy);

        // then
        assertThat(savedPolicy.getId()).isNotNull();
        assertThat(savedPolicy.getName()).isEqualTo("Welcome Bonus");
        assertThat(savedPolicy.getAmount()).isEqualTo(500);
        assertThat(savedPolicy.getRate()).isEqualTo(BigDecimal.valueOf(0.05));
    }

    @Test
    void findPointPolicyById_success() {
        // given
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("Anniversary Bonus", 1000, BigDecimal.valueOf(0.1));
        PointPolicy savedPolicy = pointPolicyRepository.save(pointPolicy);

        // when
        Optional<PointPolicy> foundPolicy = pointPolicyRepository.findById(savedPolicy.getId());

        // then
        assertThat(foundPolicy).isPresent();
        assertThat(foundPolicy.get().getName()).isEqualTo("Anniversary Bonus");
    }

    @Test
    void updatePointPolicy_success() {
        // given
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("Default Policy", 200, BigDecimal.valueOf(0.02));

        PointPolicy savedPolicy = pointPolicyRepository.save(pointPolicy);

        // when
        savedPolicy.ofUpdate("Updated Policy", 300, BigDecimal.valueOf(0.03));
        PointPolicy updatedPolicy = pointPolicyRepository.save(savedPolicy);

        // then
        assertThat(updatedPolicy.getName()).isEqualTo("Updated Policy");
        assertThat(updatedPolicy.getAmount()).isEqualTo(300);
        assertThat(updatedPolicy.getRate()).isEqualTo(BigDecimal.valueOf(0.03));
    }

    @Test
    void deletePointPolicy_success() {
        // given
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.ofCreate("Temporary Policy", 150, BigDecimal.valueOf(0.01));

        PointPolicy savedPolicy = pointPolicyRepository.save(pointPolicy);

        // when
        pointPolicyRepository.delete(savedPolicy);

        // then
        Optional<PointPolicy> foundPolicy = pointPolicyRepository.findById(savedPolicy.getId());
        assertThat(foundPolicy).isNotPresent();
    }
}
