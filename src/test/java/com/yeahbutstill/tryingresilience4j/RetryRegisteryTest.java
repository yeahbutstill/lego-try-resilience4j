package com.yeahbutstill.tryingresilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Slf4j
public class RetryRegisteryTest {

    void callme() {
        log.info("Try call me");
        throw new IllegalArgumentException("Ups error");
    }

    @Test
    void testRetryRegistery() {

        RetryRegistry registry = RetryRegistry.ofDefaults();

        Retry retry1 = registry.retry("name");
        Retry retry2 = registry.retry("name");

        Assertions.assertSame(retry1, retry2);
        retry1.executeRunnable(() -> callme());

    }

    @Test
    void retryRegistryWithConfig() {

        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5)
                .waitDuration(Duration.ofSeconds(2))
                .build();

        RetryRegistry registry = RetryRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        Retry retry1 = registry.retry("pzn", "config");
        retry1.executeRunnable(() -> callme());

    }

}
