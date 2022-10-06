package com.yeahbutstill.tryingresilience4j;

import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
class DecoratorsTest {

    @SneakyThrows
    void callMe() {
        log.info("Call me");
        Thread.sleep(1_000L);
        throw new IllegalArgumentException("Ups");
    }

    @SneakyThrows
    String fuckEm() {
        log.info("Fuckem");
        Thread.sleep(1_000L);
        throw new IllegalArgumentException("FUUUUUUUUUUUUUUCK");
    }

    @SneakyThrows
    @Test
    void decorators() {

        RateLimiter rateLimiter = RateLimiter.of("yeahbutstill-ratelimiter", RateLimiterConfig.custom()
                .limitForPeriod(5)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .build());
        Retry retry = Retry.of("yeahbutstill-retry", RetryConfig.custom()
                .maxAttempts(19)
                .waitDuration(Duration.ofMillis(10))
                .build());

        Runnable runnable = Decorators.ofRunnable(() -> callMe())
                .withRetry(retry)
                .withRateLimiter(rateLimiter)
                .decorate();

        for (int i = 0; i < 100; i++) {
            new Thread(runnable).start();
        }

        Thread.sleep(10_000);

    }

    @SneakyThrows
    @Test
    void fallback() {

        RateLimiter rateLimiter = RateLimiter.of("yeahbutstill-ratelimiter", RateLimiterConfig.custom()
                .limitForPeriod(5)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .build());
        Retry retry = Retry.of("yeahbutstill-retry", RetryConfig.custom()
                .maxAttempts(19)
                .waitDuration(Duration.ofMillis(10))
                .build());

        Supplier<String> supplier = Decorators.ofSupplier(() -> fuckEm())
                .withRetry(retry)
                .withRateLimiter(rateLimiter)
                .withFallback(throwable -> "Tetap gagal, balikin aja ke fallback")
                .decorate();

        System.out.println(supplier.get());

    }

}
