package com.tripleseven.orderapi.repository;

import com.tripleseven.orderapi.entity.paytype.PayType;
import com.tripleseven.orderapi.repository.paytypes.PayTypesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PayTypeRepositoryTest {

    @Autowired
    private PayTypesRepository payTypesRepository;

    @Test
    void testSavePayTypes() {
        // given
        PayType payType = PayType.ofCreate("Credit Card");

        // when
        PayType savedPayType = payTypesRepository.save(payType);

        // then
        assertThat(savedPayType).isNotNull();
        assertThat(savedPayType.getId()).isNotNull();
        assertThat(savedPayType.getName()).isEqualTo("Credit Card");
    }

    @Test
    void testFindPayTypesById() {
        // given
        PayType payType = PayType.ofCreate("Bank Transfer");
        PayType savedPayType = payTypesRepository.save(payType);

        // when
        Optional<PayType> retrievedPayType = payTypesRepository.findById(savedPayType.getId());

        // then
        assertThat(retrievedPayType).isPresent();
        assertThat(retrievedPayType.get().getName()).isEqualTo("Bank Transfer");
    }

    @Test
    void testUpdatePayTypes() {
        // given
        PayType payType = PayType.ofCreate("Old Name");
        PayType savedPayType = payTypesRepository.save(payType);

        // when
        savedPayType.ofUpdate("Updated Name");
        PayType updatedPayType = payTypesRepository.save(savedPayType);

        // then
        assertThat(updatedPayType.getName()).isEqualTo("Updated Name");
    }

    @Test
    void testDeletePayTypes() {
        // given
        PayType payType = PayType.ofCreate("PayPal");
        PayType savedPayType = payTypesRepository.save(payType);

        // when
        payTypesRepository.deleteById(savedPayType.getId());

        // then
        Optional<PayType> deletedPayType = payTypesRepository.findById(savedPayType.getId());
        assertThat(deletedPayType).isNotPresent();
    }
}