package com.yeahbutstill.tryingresilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
class RetryConfigTest {

    @Test
    void craeteSupplier() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5)
                .waitDuration(Duration.ofSeconds(2))
                .retryExceptions(IllegalArgumentException.class)
                .build();

        Retry retry = Retry.of("yeahbutstill", config);

        Supplier<String> supplier = Retry.decorateSupplier(retry, () -> hello());
        String result = supplier.get();
        System.out.println(result);
    }

    private String hello() {
        log.info("call say hello");
        throw new IllegalArgumentException("Ups error say hello");
    }

}
