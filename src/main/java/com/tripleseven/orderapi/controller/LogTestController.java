package com.tripleseven.orderapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "LogBack test-Controller", description = "로그백 테스트 컨트롤러")
@RestController
public class LogTestController {

    private static final Logger logger = LoggerFactory.getLogger(LogTestController.class);

    @GetMapping("/test-error")
    public String testErrorLogging() {
        logger.error("This is an ERROR log message for testing");
        return "Error log sent!";
    }
}