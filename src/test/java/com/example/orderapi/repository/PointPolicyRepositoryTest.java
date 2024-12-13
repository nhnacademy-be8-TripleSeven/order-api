package com.example.orderapi.repository;

import com.example.orderapi.entity.PointPolicy.PointPolicy;
import com.example.orderapi.repository.pointpolicy.PointPolicyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PointPolicyRepositoryTest {

    @Autowired
    private PointPolicyRepository pointPolicyRepository;

    @Test
    void testSavePolicy(){
        //given
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.setName("test");
        pointPolicy.setAmount(500);

        PointPolicy pointPolicy1 = new PointPolicy();
        pointPolicy1.setName("test1");
        pointPolicy1.setRate(BigDecimal.valueOf(1.5));

        //when
        PointPolicy savedPolicy = pointPolicyRepository.save(pointPolicy);
        PointPolicy savedPolicy1 = pointPolicyRepository.save(pointPolicy1);

        //then
        assertNotNull(savedPolicy.getId());
        assertEquals(pointPolicy.getName(), savedPolicy.getName());
        assertNotNull(savedPolicy1.getId());
        assertEquals(pointPolicy1.getRate(), savedPolicy1.getRate());
    }

    @Test
    void testDeletePolicy(){
        //given
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.setName("test");
        pointPolicy.setAmount(500);
        PointPolicy savedPolicy = pointPolicyRepository.save(pointPolicy);

        //when
        pointPolicyRepository.deleteById(savedPolicy.getId());

        //then
        assertNull(pointPolicyRepository.findById(savedPolicy.getId()).orElse(null));
    }

    @Test
    void testSelectPolicy(){
        //given
        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.setName("test");
        pointPolicy.setAmount(500);

        PointPolicy pointPolicy1 = new PointPolicy();
        pointPolicy1.setName("test1");
        pointPolicy1.setRate(BigDecimal.valueOf(1.5));

        //when
        pointPolicyRepository.save(pointPolicy);
        pointPolicyRepository.save(pointPolicy1);

        //then

        assertEquals("test", pointPolicyRepository.findById(pointPolicy.getId()).orElse(null).getName());
        assertEquals("test1", pointPolicyRepository.findById(pointPolicy1.getId()).orElse(null).getName());
        assertEquals(500, pointPolicyRepository.findById(pointPolicy.getId()).orElse(null).getAmount());
        assertEquals(BigDecimal.valueOf(1.5), pointPolicyRepository.findById(pointPolicy1.getId()).orElse(null).getRate());

    }

}