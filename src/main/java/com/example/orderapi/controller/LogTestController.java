package com.example.orderapi.controller;

import com.example.orderapi.client.BookCouponApiClient;
import com.example.orderapi.client.MemberApiClient;
import com.example.orderapi.dto.wrapping.WrappingCreateRequest;
import com.example.orderapi.dto.wrapping.WrappingResponse;
import com.example.orderapi.entity.wrapping.Wrapping;
import com.example.orderapi.service.wrapping.WrappingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogTestController {

    private static final Logger logger = LoggerFactory.getLogger(LogTestController.class);
    private final BookCouponApiClient bookCouponApiClient;
    private final MemberApiClient memberApiClient;
    @GetMapping("/test-error")
    public String testErrorLogging() {
        logger.error("This is an ERROR log message for testing");
        return "Error log sent!";
    }

    private static final String EXCHANGE_NAME = "nhn24.pay.exchange";

    @Autowired
    RabbitTemplate rabbitTemplate;
    @GetMapping("/orders/processing")
    public String samplePublish() {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "order.routing.key", "order + SpringBoot = Success");
        return "order";
    }

    @GetMapping("/orders/point")
    public String pointPublish() {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "point.routing.key", "point + SpringBoot = Success");
        return "point";
    }

    @GetMapping("/orders/cart")
    public String cartPublish() {
        memberApiClient.getMember();
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "cart.routing.key", "cart + SpringBoot = Success");
        return "cart";
    }
}