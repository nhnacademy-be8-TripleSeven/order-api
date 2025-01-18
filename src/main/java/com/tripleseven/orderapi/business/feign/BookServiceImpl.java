package com.tripleseven.orderapi.business.feign;

import com.tripleseven.orderapi.client.BookCouponApiClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookCouponApiClient bookCouponApiClient;

    // 2초 마다 FeignException 시 2번 더 재시도
    @Override
    @Retryable(value = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void useCoupon(Long couponId) {
        bookCouponApiClient.updateUseCoupon(couponId);
    }

    @Recover
    public void recover(FeignException e, Long couponId) {
        log.error("Failed to use coupon after retries: {}", couponId);
        throw e;
    }
}
