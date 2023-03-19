package com.aa.checkout.config.circuitbreaker;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Configuration
public class CircuitBreakerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerConfiguration.class);
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerConfiguration(final CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostConstruct
    public void logStateTransitionsOfCircuitBreakers() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Resilience4J [template-replace:servicename] Circuit Breaker has : {}", circuitBreakerRegistry.circuitBreaker("[templateReplaceCircuitBreaker]"));
        }
        circuitBreakerRegistry.circuitBreaker("[templateReplaceCircuitBreaker]").getEventPublisher().onStateTransition(errorEvent -> {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("[template-replace:servicename] Circuit Breaker State: " + errorEvent);
            }
        });
    }
}
