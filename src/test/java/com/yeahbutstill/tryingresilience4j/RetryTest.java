package com.yeahbutstill.tryingresilience4j;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

@Slf4j
class RetryTest {

    void callMe() {
        log.info("Try call me");
        throw new IllegalArgumentException("Ups error");
    }

    @Test
    void createNewRetry() {
        Retry retry = Retry.ofDefaults("yeahbutstill");
        Runnable runnable = Retry.decorateRunnable(retry, () -> callMe());
    }

    @Test
    void craeteSupplier() {
        Retry retry = Retry.ofDefaults("maya");
        Supplier<String> supplier = Retry.decorateSupplier(retry, () -> hello());
        String result = supplier.get();
    }

    private String hello() {
        log.info("call say hello");
        throw new IllegalArgumentException("Ups error say hello");
    }

}
