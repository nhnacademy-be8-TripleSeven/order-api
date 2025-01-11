package com.tripleseven.orderapi.repository.querydsl;

import com.tripleseven.orderapi.dto.order.DeliveryInfoDTO;
import com.tripleseven.orderapi.entity.deliveryinfo.DeliveryInfo;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.repository.deliveryinfo.DeliveryInfoRepository;
import com.tripleseven.orderapi.repository.deliveryinfo.querydsl.QueryDslDeliveryInfoRepositoryImpl;
import com.tripleseven.orderapi.repository.ordergroup.OrderGroupRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Transactional
@Import(QueryDslDeliveryInfoRepositoryImpl.class)
class QueryDslDeliveryInfoTest {

    @Autowired
    private QueryDslDeliveryInfoRepositoryImpl queryDslDeliveryInfoRepository;

    @Autowired
    private OrderGroupRepository orderGroupRepository;

    @Autowired
    private DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void getDeliveryInfo_shouldReturnCorrectDeliveryInfoDTO() {
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.ofCreate(1L, "Test Orderer", "Test Recipient", "01012345678", "02012345678", 3000, "Test Address", null);
        OrderGroup savedOrderGroup = orderGroupRepository.save(orderGroup);

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.ofCreate(savedOrderGroup);
        deliveryInfo.ofUpdate("Test Delivery Name", 1234567890, LocalDate.now());
        deliveryInfoRepository.save(deliveryInfo);

        entityManager.flush();
        entityManager.clear();

        DeliveryInfoDTO deliveryInfoDTO = queryDslDeliveryInfoRepository.getDeliveryInfo(savedOrderGroup.getId());

        assertNotNull(deliveryInfoDTO);
        assertEquals("Test Delivery Name", deliveryInfoDTO.getDeliveryInfoName());
        assertEquals(1234567890, deliveryInfoDTO.getInvoiceNumber());
        assertEquals(savedOrderGroup.getId(), deliveryInfoDTO.getOrderId());
        assertEquals("Test Recipient", deliveryInfoDTO.getRecipientName());
    }
}
