package com.example.orderapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogTestController {

    private static final Logger logger = LoggerFactory.getLogger(LogTestController.class);

    @GetMapping("/test-error")
    public String testErrorLogging() {
        logger.warn("This is an ERROR log message for testing");
        return "Error log sent!";
    }
}

