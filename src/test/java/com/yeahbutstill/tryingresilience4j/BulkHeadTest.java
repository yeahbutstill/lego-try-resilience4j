package com.yeahbutstill.tryingresilience4j;

import io.github.resilience4j.bulkhead.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Slf4j
class BulkHeadTest {

    private final AtomicLong counter = new AtomicLong(0L);

    @SneakyThrows
    void slow() {

        long value = counter.incrementAndGet();
        log.info("Slow {}", value);
        Thread.sleep(1_000L);

    }

    @Test
    void testSemaphore() throws InterruptedException {

        Bulkhead bulkhead = Bulkhead.ofDefaults("yeahbutstill");

        bulkhead.getMetrics().getAvailableConcurrentCalls();
        bulkhead.getMetrics().getMaxAllowedConcurrentCalls();

        for (int i = 0; i < 1_000; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);

    }

    @Test
    void testThreadPool() {

        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));
        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.ofDefaults("yeahbutstill");
        for (int i = 0; i < 1000; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }

    }

    @SneakyThrows
    @Test
    void testSemaphoreConfig() {

        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofSeconds(5))
                .build();

        Bulkhead bulkhead = Bulkhead.of("yeahbutstill", config);

        for (int i = 0; i < 10; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);

    }

    @SneakyThrows
    @Test
    void testThreadPoolConfig() {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(5)
                .coreThreadPoolSize(5)
                .queueCapacity(100)
                .build();

        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));
        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.of("yeahbutstill", config);

        for (int i = 0; i < 20; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }

        Thread.sleep(10_000);
    }

    @SneakyThrows
    @Test
    void testSemaphoreRegistry() {

        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofSeconds(5))
                .build();

        BulkheadRegistry registry = BulkheadRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        Bulkhead bulkhead = registry.bulkhead("yeahbutstill", config);

        for (int i = 0; i < 10; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, () -> slow());
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);

    }

    @SneakyThrows
    @Test
    void testThreadPoolRegistry() {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(5)
                .coreThreadPoolSize(5)
                .queueCapacity(100)
                .build();

        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

        ThreadPoolBulkheadRegistry registry = ThreadPoolBulkheadRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        ThreadPoolBulkhead bulkhead = registry.bulkhead("yeahbutstill", "config");

        for (int i = 0; i < 20; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }

        Thread.sleep(10_000L);
    }

}
