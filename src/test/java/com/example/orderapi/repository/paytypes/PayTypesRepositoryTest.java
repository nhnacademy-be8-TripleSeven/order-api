package com.example.orderapi.repository.paytypes;

import com.example.orderapi.entity.paytypes.PayTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")

public class PayTypesRepositoryTest {

    @Autowired
    private PayTypesRepository payTypesRepository;

    private PayTypes payType;

    @BeforeEach
    public void setUp() {
        // 테스트용 데이터 초기화
        payType = new PayTypes();
        payType.setName("Credit Card");
        payTypesRepository.save(payType);  // PayType을 데이터베이스에 저장
    }

    @Test
    public void testSavePayType() {
        PayTypes newPayType = new PayTypes();
        newPayType.setName("PayPal");

        PayTypes savedPayType = payTypesRepository.save(newPayType);

        assertNotNull(savedPayType.getId());  // 저장 후 id 값이 null이 아니어야 함
        assertEquals("PayPal", savedPayType.getName());
    }

    @Test
    public void testFindById() {
        Optional<PayTypes> foundPayType = payTypesRepository.findById(payType.getId());

        assertTrue(foundPayType.isPresent());
        assertEquals(payType.getName(), foundPayType.get().getName());
    }

    @Test
    public void testFindAll() {
        List<PayTypes> payTypesList = payTypesRepository.findAll();

        assertNotNull(payTypesList);
        assertFalse(payTypesList.isEmpty());  // 데이터가 있어야 함
    }

    @Test
    public void testDeleteById() {
        Long payTypeId = payType.getId();

        payTypesRepository.deleteById(payTypeId);
        Optional<PayTypes> deletedPayType = payTypesRepository.findById(payTypeId);

        assertFalse(deletedPayType.isPresent());  // 삭제 후 id로 찾을 수 없어야 함
    }


}