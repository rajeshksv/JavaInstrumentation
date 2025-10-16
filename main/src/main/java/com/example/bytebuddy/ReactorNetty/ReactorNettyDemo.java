package com.example.bytebuddy.ReactorNetty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Simple Reactor Netty Demo that demonstrates:
 * 1. HTTP Client making outbound HTTP calls
 * 2. Reactive programming patterns
 */
public class ReactorNettyDemo {
    
    private static final Logger logger = LoggerFactory.getLogger(ReactorNettyDemo.class);
    private static final String EXTERNAL_API_URL = "https://jsonplaceholder.typicode.com/posts/1";
    
    private static final HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(10));
    
    public static void main(String[] args) {
        logger.info("Starting Reactor Netty Demo");
        
        // Demonstrate outbound HTTP calls
        demonstrateOutboundCalls();
        
        // Demonstrate advanced patterns
        demonstrateAdvancedPatterns();
        
        logger.info("Demo completed");
    }
    
    /**
     * Demonstrate outbound HTTP calls
     */
    private static void demonstrateOutboundCalls() {
        logger.info("=== Outbound HTTP Calls Demo ===");
        
        // Simple GET request
        logger.info("1. Simple GET request");
        httpClient.get()
                .uri(EXTERNAL_API_URL)
                .responseSingle((response, body) -> {
                    logger.info("Received response with status: {}", response.status());
                    return body.asString();
                })
                .doOnSuccess(data -> logger.info("Successfully received data: {}", data.substring(0, Math.min(100, data.length())) + "..."))
                .doOnError(error -> logger.error("Error making request", error))
                .subscribe();
        
        // Multiple concurrent requests
        logger.info("2. Multiple concurrent requests");
        Mono.zip(
                httpClient.get().uri("https://jsonplaceholder.typicode.com/posts/1").responseSingle((response, body) -> body.asString()),
                httpClient.get().uri("https://jsonplaceholder.typicode.com/posts/2").responseSingle((response, body) -> body.asString()),
                httpClient.get().uri("https://jsonplaceholder.typicode.com/posts/3").responseSingle((response, body) -> body.asString())
        )
        .subscribe(
                tuple -> logger.info("Concurrent responses received: {} items", tuple.getT1().length() + tuple.getT2().length() + tuple.getT3().length()),
                error -> logger.error("Concurrent requests error", error)
        );
        
        // Wait a bit to see the results
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Demonstrate advanced patterns
     */
    public static void demonstrateAdvancedPatterns() {
        logger.info("=== Advanced Reactor Netty Patterns ===");
        
        // Pattern 1: Circuit breaker-like behavior
        logger.info("Pattern 1: Circuit breaker-like behavior");
        httpClient.get()
                .uri("https://httpbin.org/delay/2")
                .responseSingle((response, body) -> body.asString())
                .timeout(Duration.ofSeconds(1))
                .onErrorResume(throwable -> {
                    logger.warn("Request timed out, returning fallback response");
                    return Mono.just("{\"error\":\"timeout\",\"fallback\":true}");
                })
                .subscribe(
                        data -> logger.info("Circuit breaker response: {}", data),
                        error -> logger.error("Circuit breaker error", error)
                );
        
        // Pattern 2: Retry logic
        logger.info("Pattern 2: Retry logic");
        httpClient.get()
                .uri("https://jsonplaceholder.typicode.com/posts/1")
                .responseSingle((response, body) -> {
                    if (response.status().code() >= 400) {
                        throw new RuntimeException("HTTP error: " + response.status());
                    }
                    return body.asString();
                })
                .retry(3)
                .subscribe(
                        data -> logger.info("Retry successful: {}", data.substring(0, Math.min(50, data.length())) + "..."),
                        error -> logger.error("Retry failed", error)
                );
        
        // Wait a bit to see the results
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
