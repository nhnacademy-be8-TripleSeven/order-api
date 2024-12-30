package com.tripleseven.orderapi.repository;

import com.tripleseven.orderapi.entity.pay.Pay;
import com.tripleseven.orderapi.repository.pay.PayRepository;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PayRepositoryTest {

    @Autowired
    private PayRepository payRepository;

    @Test
    void testSavePay() {
        // given
        JSONObject response = new JSONObject();
        response.put("orderId", "ORDER123");
        response.put("approvedAt", OffsetDateTime.now().toString());
        response.put("balanceAmount", "5000");
        response.put("status", "APPROVED");
        response.put("paymentKey", "PAYMENT_KEY_123");

        Pay pay = new Pay();
        pay.ofCreate(response);

        // when
        Pay savedPay = payRepository.save(pay);

        // then
        assertThat(savedPay).isNotNull();
        assertThat(savedPay.getId()).isNotNull();
        assertThat(savedPay.getOrderId()).isEqualTo("ORDER123");
        assertThat(savedPay.getPrice()).isEqualTo(5000);
        assertThat(savedPay.getStatus()).isEqualTo("APPROVED");
        assertThat(savedPay.getPaymentKey()).isEqualTo("PAYMENT_KEY_123");
        assertThat(savedPay.getRequestedAt()).isNotNull();
    }

    @Test
    void testFindPayById() {
        // given
        JSONObject response = new JSONObject();
        response.put("orderId", "ORDER456");
        response.put("approvedAt", OffsetDateTime.now().toString());
        response.put("balanceAmount", "10000");
        response.put("status", "PENDING");
        response.put("paymentKey", "PAYMENT_KEY_456");

        Pay pay = new Pay();
        pay.ofCreate(response);
        Pay savedPay = payRepository.save(pay);

        // when
        Optional<Pay> retrievedPay = payRepository.findById(savedPay.getId());

        // then
        assertThat(retrievedPay).isPresent();
        Pay foundPay = retrievedPay.get();
        assertThat(foundPay.getOrderId()).isEqualTo("ORDER456");
        assertThat(foundPay.getPrice()).isEqualTo(10000);
        assertThat(foundPay.getStatus()).isEqualTo("PENDING");
        assertThat(foundPay.getPaymentKey()).isEqualTo("PAYMENT_KEY_456");
    }

    @Test
    void testUpdatePay() {
        // given
        JSONObject createResponse = new JSONObject();
        createResponse.put("orderId", "ORDER789");
        createResponse.put("approvedAt", OffsetDateTime.now().toString());
        createResponse.put("balanceAmount", "20000");
        createResponse.put("status", "PENDING");
        createResponse.put("paymentKey", "PAYMENT_KEY_789");

        Pay pay = new Pay();
        pay.ofCreate(createResponse);
        Pay savedPay = payRepository.save(pay);

        JSONObject updateResponse = new JSONObject();
        updateResponse.put("orderId", "ORDER789_UPDATED");
        updateResponse.put("approvedAt", OffsetDateTime.now().toString());
        updateResponse.put("balanceAmount", "15000");
        updateResponse.put("status", "APPROVED");
        updateResponse.put("paymentKey", "PAYMENT_KEY_789_UPDATED");

        // when
        savedPay.ofUpdate(updateResponse);
        Pay updatedPay = payRepository.save(savedPay);

        // then
        assertThat(updatedPay.getOrderId()).isEqualTo("ORDER789_UPDATED");
        assertThat(updatedPay.getPrice()).isEqualTo(15000);
        assertThat(updatedPay.getStatus()).isEqualTo("APPROVED");
        assertThat(updatedPay.getPaymentKey()).isEqualTo("PAYMENT_KEY_789_UPDATED");
    }

    @Test
    void testDeletePay() {
        // given
        JSONObject response = new JSONObject();
        response.put("orderId", "ORDER_TO_DELETE");
        response.put("approvedAt", OffsetDateTime.now().toString());
        response.put("balanceAmount", "3000");
        response.put("status", "CANCELED");
        response.put("paymentKey", "PAYMENT_KEY_TO_DELETE");

        Pay pay = new Pay();
        pay.ofCreate(response);
        Pay savedPay = payRepository.save(pay);

        // when
        payRepository.deleteById(savedPay.getId());

        // then
        Optional<Pay> deletedPay = payRepository.findById(savedPay.getId());
        assertThat(deletedPay).isNotPresent();
    }
}