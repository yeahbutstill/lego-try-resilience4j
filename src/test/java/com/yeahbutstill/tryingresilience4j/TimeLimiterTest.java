package com.yeahbutstill.tryingresilience4j;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
class TimeLimiterTest {

    @SneakyThrows
    public Long slow() {
        log.info("Start Slow");
        Thread.sleep(10_000L);
        log.info("End Slow");
        return 10_000L;
    }

    @SneakyThrows
    @Test
    void testTimeLimiter() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Long> future = executorService.submit(() -> slow());

        TimeLimiter limiter = TimeLimiter.ofDefaults("yeahbutstill");
        Callable<Long> callable = TimeLimiter.decorateFutureSupplier(limiter, () -> future);

        callable.call();
    }

    @SneakyThrows
    @Test
    void testTimeLimiterConfig() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Long> future = executorService.submit(() -> slow());

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(10))
                .cancelRunningFuture(true)
                .build();

        TimeLimiter limiter = TimeLimiter.of("yeahbutstill", config);
        Callable<Long> callable = TimeLimiter.decorateFutureSupplier(limiter, () -> future);

        callable.call();
    }

    @SneakyThrows
    @Test
    void testTimeLimiterRegistry() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Long> future = executorService.submit(this::slow);

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(10))
                .cancelRunningFuture(true)
                .build();

        TimeLimiterRegistry registry = TimeLimiterRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        TimeLimiter limiter = registry.timeLimiter("yeahbutstill", "config");
        Callable<Long> callable = TimeLimiter.decorateFutureSupplier(limiter, () -> future);

        callable.call();
    }

}
