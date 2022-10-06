package com.yeahbutstill.tryingresilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CircuitBreakerTest {

    void callMe() {
        log.info("Call me");
        throw new IllegalArgumentException("Ups");
    }

    @Test
    void testCircuitBreaker() {
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("yeahbutsill");

        for (int i = 0; i < 200; i++) {
            try {
                Runnable runnable = CircuitBreaker.decorateRunnable(circuitBreaker, () -> callMe());
                runnable.run();
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
            }
        }
    }

    @Test
    void circuitBreakerConfig () {

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .failureRateThreshold(10f)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(10)
                .build();
        CircuitBreaker circuitBreaker = CircuitBreaker.of("yeahbutstill", config);

        for (int i = 0; i < 100; i++) {
            try {
                Runnable runnable = CircuitBreaker.decorateRunnable(circuitBreaker, () -> callMe());
                runnable.run();
            } catch (Exception e) {
                log.error("Ups: {}", e.getMessage());
            }
        }

    }

    @Test
    void circuitBreakerRegistry () {

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .failureRateThreshold(10f)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(10)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        registry.addConfiguration("config", config);

        CircuitBreaker circuitBreaker = registry.circuitBreaker("yeahbutstill", "config");

        for (int i = 0; i < 100; i++) {
            try {
                Runnable runnable = CircuitBreaker.decorateRunnable(circuitBreaker, () -> callMe());
                runnable.run();
            } catch (Exception e) {
                log.error("Ups: {}", e.getMessage());
            }
        }

    }

}
