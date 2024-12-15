package com.example.orderapi.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderGroupServiceTest {
//
//    // target
//    @InjectMocks
//    OrderGroupServiceImpl orderGroupService;
//
//
//    // dependency
//    @Mock
//    OrderGroupRepository orderGroupRepository;
//
//    Long id;
//
//
//    OrderGroup OrderGroup;
//
//    @BeforeEach
//    void setUp() {
//
//        String dateString = "2018-08-23 10:30:00";
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
//
//        ZoneId zoneId = ZoneId.of("Asia/Seoul");
//        ZonedDateTime orderedAt = localDateTime.atZone(zoneId);
//
//        id = 1L;
//        Long userId = 2L;
//        Long wrappingId = 1L;
//        Long deliveryInfoId = null;
//        String ordererName = "orderer1";
//        String recipientName = "recipient1";
//        String recipientPhone = "01012345678";
//        int deliveryPrice = 5000;
//
//        OrderGroup = new OrderGroup(id, userId, wrappingId, deliveryInfoId, ordererName, orderedAt, recipientName, recipientPhone, deliveryPrice);
//    }
//
//
//    @Test
//    void getById(){
//        //given
////        given(orderGroupRepository.findById(id)).willReturn(Optional.of(new OrderGroupResponse()));
//
//        //when
//        OrderGroupResponse actual = orderGroupService.getById(id);
//
//        //then
//        Assertions.assertEquals(actual, OrderGroup);
//    }
}
