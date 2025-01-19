package com.tripleseven.orderapi.common.scheduler;

import com.tripleseven.orderapi.service.orderdetail.OrderDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryScheduler {
    private final OrderDetailService orderDetailService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 정각 실행
    public void processOverdueDeliveries() {
        log.info("Starting overdue delivery processing...");

        // 7일 경과한 "배송중" 항목을 "배송완료"로 변경
        orderDetailService.completeOverdueShipments(Duration.ofDays(7));

        log.info("Finished overdue delivery processing.");
    }
}
