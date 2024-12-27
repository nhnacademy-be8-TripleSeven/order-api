package com.tripleseven.orderapi.common.rabbit;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RetryStateService {
    private final ConcurrentHashMap<String, AtomicInteger> retryCounts = new ConcurrentHashMap<>();

    public AtomicInteger getRetryCount(String routingKey) {
        return retryCounts.computeIfAbsent(routingKey, key -> new AtomicInteger(0));
    }

    public void resetRetryCount(String messageId) {
        retryCounts.remove(messageId);
    }
}
