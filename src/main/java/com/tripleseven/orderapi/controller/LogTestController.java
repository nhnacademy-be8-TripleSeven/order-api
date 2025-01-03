package com.tripleseven.orderapi.controller;

import com.tripleseven.orderapi.business.pay.OrderProcessingStrategy;
import com.tripleseven.orderapi.dto.cartitem.CartItemDTO;
import com.tripleseven.orderapi.dto.ordergroup.OrderGroupCreateRequestDTO;
import com.tripleseven.orderapi.entity.ordergroup.OrderGroup;
import com.tripleseven.orderapi.entity.pointhistory.HistoryTypes;
import com.tripleseven.orderapi.entity.pointhistory.PointHistory;
import com.tripleseven.orderapi.entity.wrapping.Wrapping;
import com.tripleseven.orderapi.repository.pointhistory.PointHistoryRepository;
import com.tripleseven.orderapi.repository.wrapping.WrappingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LogTestController {

    private static final Logger logger = LoggerFactory.getLogger(LogTestController.class);
    private final OrderProcessingStrategy orderProcessingStrategy;
    private final WrappingRepository wrappingRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PointHistoryRepository pointHistoryRepository;

    @GetMapping("/test-error")
    public String testErrorLogging() {
        logger.error("This is an ERROR log message for testing");
        return "Error log sent!";
    }


    @GetMapping("/test-rabbit")
    public void testRabbitMQ(@RequestHeader("X-USER") Long userId) {

        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.ofCreateTest();
        List<CartItemDTO> cartItemDTOList = new ArrayList<>();
        cartItemDTOList.add(cartItemDTO);
        redisTemplate.opsForHash().put(userId.toString(), "CartItems", cartItemDTOList);

        Wrapping wrapping = new Wrapping();
        wrapping.ofCreate("Test Wrapping", 100);
        Wrapping savedWrapping = wrappingRepository.save(wrapping);

        OrderGroup orderGroup = new OrderGroup();
        orderGroup.ofCreate(userId, "Test Ordered", "Test Recipient", "01012345678", 1000, "Test Address", wrapping);
        OrderGroupCreateRequestDTO orderGroupCreateRequestDTO =
                new OrderGroupCreateRequestDTO(
                        savedWrapping.getId(),
                        orderGroup.getOrderedName(),
                        orderGroup.getRecipientName(),
                        orderGroup.getRecipientPhone(),
                        orderGroup.getDeliveryPrice(),
                        orderGroup.getAddress());


        PointHistory pointHistory = new PointHistory(
                HistoryTypes.EARN,
                12345,
                LocalDateTime.now(),
                "test",
                userId,
                orderGroup
        );

        pointHistoryRepository.save(pointHistory);

        orderProcessingStrategy.processMemberOrder(userId, orderGroupCreateRequestDTO);
    }
}