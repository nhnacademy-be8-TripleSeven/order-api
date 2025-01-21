package com.tripleseven.orderapi.repository;

import com.tripleseven.orderapi.dto.pay.PaymentDTO;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pay.Pay;
import com.tripleseven.orderapi.entity.pay.PaymentStatus;
import com.tripleseven.orderapi.entity.paytype.PayType;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Rollback
class PayRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("1. Pay 엔터티 저장 및 조회 테스트")
    void saveAndFindPay() {
        // Given: Wrapping 객체 저장
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate("기본 포장", 3000);
        entityManager.persist(wrapping);

        // Given: OrderGroup 객체 저장
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.ofCreate(1L, "김철수", "박영희", "010-1234-5678", "02-1234-5678", 3000, "서울 강남구", wrapping);
        entityManager.persist(orderGroup);

        // ✅ Given: PayType 객체 생성 및 저장 (`name` 필수 값 검증)
        PayType payType = PayType.ofCreate("신용카드"); // ✅ `name` 값 설정
        entityManager.persist(payType);

        // Given: Pay 객체 생성 (`ofCreate()` 사용)
        Pay pay = new Pay();
        PaymentDTO paymentDTO = new PaymentDTO(1L, LocalDate.now(), 50000, PaymentStatus.READY, "test-key");
        pay.ofCreate(paymentDTO, orderGroup, payType);
        entityManager.persist(pay);
        entityManager.flush();
        entityManager.clear();

        // When: Pay 객체 조회
        Pay foundPay = entityManager.find(Pay.class, pay.getId());

        // Then: Pay 객체 검증
        assertThat(foundPay).isNotNull();
        assertThat(foundPay.getOrderId()).isEqualTo(1L);
        assertThat(foundPay.getPrice()).isEqualTo(50000);
        assertThat(foundPay.getStatus()).isEqualTo(PaymentStatus.READY);
        assertThat(foundPay.getPaymentKey()).isEqualTo("test-key");
        assertThat(foundPay.getOrderGroup()).isNotNull();
        assertThat(foundPay.getOrderGroup().getOrderedName()).isEqualTo("김철수");
        assertThat(foundPay.getPayType()).isNotNull();
        assertThat(foundPay.getPayType().getName()).isEqualTo("신용카드"); // ✅ PayType 검증
    }

    @Test
    @DisplayName("2. Pay 엔터티의 결제 정보 갱신 테스트")
    void updatePayInfo() {
        // Given: Wrapping, OrderGroup, PayType 저장
        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate("기본 포장", 3000);
        entityManager.persist(wrapping);

        OrderGroup orderGroup = new OrderGroup();
        orderGroup.ofCreate(1L, "김철수", "박영희", "010-1234-5678", "02-1234-5678", 3000, "서울 강남구", wrapping);
        entityManager.persist(orderGroup);

        // ✅ Given: PayType 객체 생성 및 저장 (`name` 필수 값 설정)
        PayType payType = PayType.ofCreate("신용카드"); // ✅ `name` 값 설정
        entityManager.persist(payType);

        // Given: Pay 객체 생성 및 저장 (`ofCreate()` 사용)
        Pay pay = new Pay();
        PaymentDTO initialDTO = new PaymentDTO(1L, LocalDate.now(), 50000, PaymentStatus.IN_PROGRESS, "test-key");
        pay.ofCreate(initialDTO, orderGroup, payType);
        entityManager.persist(pay);
        entityManager.flush();
        entityManager.clear();

        // When: 결제 상태 및 정보 업데이트 (`ofUpdate()` 사용)
        Pay foundPay = entityManager.find(Pay.class, pay.getId());
        PaymentDTO updatedDTO = new PaymentDTO(2L, LocalDate.now(), 70000, PaymentStatus.CANCELED, "updated-key");
        foundPay.ofUpdate(updatedDTO);
        entityManager.flush();
        entityManager.clear();

        // Then: Pay 객체 갱신 정보 검증
        Pay updatedPay = entityManager.find(Pay.class, pay.getId());
        assertThat(updatedPay.getOrderId()).isEqualTo(2L);
        assertThat(updatedPay.getPrice()).isEqualTo(70000);
        assertThat(updatedPay.getStatus()).isEqualTo(PaymentStatus.CANCELED); // ✅ PaymentStatus 변경 확인
        assertThat(updatedPay.getPaymentKey()).isEqualTo("updated-key");
    }
}