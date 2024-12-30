package com.tripleseven.orderapi.repository;

import com.tripleseven.orderapi.entity.paytypes.PayTypes;
import com.tripleseven.orderapi.repository.paytypes.PayTypesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PayTypesRepositoryTest {

    @Autowired
    private PayTypesRepository payTypesRepository;

    @Test
    void testSavePayTypes() {
        // given
        PayTypes payType = PayTypes.ofCreate("Credit Card");

        // when
        PayTypes savedPayType = payTypesRepository.save(payType);

        // then
        assertThat(savedPayType).isNotNull();
        assertThat(savedPayType.getId()).isNotNull();
        assertThat(savedPayType.getName()).isEqualTo("Credit Card");
    }

    @Test
    void testFindPayTypesById() {
        // given
        PayTypes payType = PayTypes.ofCreate("Bank Transfer");
        PayTypes savedPayType = payTypesRepository.save(payType);

        // when
        Optional<PayTypes> retrievedPayType = payTypesRepository.findById(savedPayType.getId());

        // then
        assertThat(retrievedPayType).isPresent();
        assertThat(retrievedPayType.get().getName()).isEqualTo("Bank Transfer");
    }

    @Test
    void testUpdatePayTypes() {
        // given
        PayTypes payType = PayTypes.ofCreate("Old Name");
        PayTypes savedPayType = payTypesRepository.save(payType);

        // when
        savedPayType.ofUpdate("Updated Name");
        PayTypes updatedPayType = payTypesRepository.save(savedPayType);

        // then
        assertThat(updatedPayType.getName()).isEqualTo("Updated Name");
    }

    @Test
    void testDeletePayTypes() {
        // given
        PayTypes payType = PayTypes.ofCreate("PayPal");
        PayTypes savedPayType = payTypesRepository.save(payType);

        // when
        payTypesRepository.deleteById(savedPayType.getId());

        // then
        Optional<PayTypes> deletedPayType = payTypesRepository.findById(savedPayType.getId());
        assertThat(deletedPayType).isNotPresent();
    }
}