package com.example.bytebuddy.ReactorNetty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Simple Reactor Netty TCP Example that demonstrates:
 * 1. TCP Client making outbound TCP calls
 * 2. Reactive programming patterns with TCP
 */
public class SimpleTcpExample {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleTcpExample.class);
    private static final String EXTERNAL_TCP_HOST = "httpbin.org";
    private static final int EXTERNAL_TCP_PORT = 80;
    
    public static void main(String[] args) {
        logger.info("Starting Simple Reactor Netty TCP Example");
        
        // Demonstrate outbound TCP calls
        demonstrateOutboundTcpCalls();
        
        // Demonstrate advanced patterns
        demonstrateAdvancedPatterns();
        
        logger.info("Example completed");
    }
    
    /**
     * Demonstrate outbound TCP calls
     */
    private static void demonstrateOutboundTcpCalls() {
        logger.info("=== Outbound TCP Calls Demo ===");
        
        CountDownLatch latch = new CountDownLatch(3);
        
        // Simple TCP request
        logger.info("1. Simple TCP request");
        makeTcpCall("Hello from Reactor Netty TCP!")
                .doOnSuccess(data -> logger.info("Successfully received TCP response: {}", data.substring(0, Math.min(200, data.length())) + "..."))
                .doOnError(error -> logger.error("Error making TCP request", error))
                .doFinally(signalType -> latch.countDown())
                .subscribe();
        
        // Multiple concurrent TCP requests
        logger.info("2. Multiple concurrent TCP requests");
        Mono.zip(
                makeTcpCall("Request 1"),
                makeTcpCall("Request 2"),
                makeTcpCall("Request 3")
        )
        .doOnSuccess(tuple -> logger.info("Concurrent TCP responses received: {} items", tuple.getT1().length() + tuple.getT2().length() + tuple.getT3().length()))
        .doOnError(error -> logger.error("Concurrent TCP requests error", error))
        .doFinally(signalType -> latch.countDown())
        .subscribe();
        
        // Additional TCP request
        logger.info("3. Additional TCP request");
        makeTcpCall("{\"type\":\"test\",\"data\":\"TCP communication\"}")
                .doOnSuccess(data -> logger.info("Additional TCP successful: {}", data.substring(0, Math.min(100, data.length())) + "..."))
                .doOnError(error -> logger.error("Additional TCP request failed", error))
                .doFinally(signalType -> latch.countDown())
                .subscribe();
        
        try {
            // Wait for all requests to complete
            boolean completed = latch.await(30, TimeUnit.SECONDS);
            if (completed) {
                logger.info("All outbound TCP calls completed successfully!");
            } else {
                logger.warn("Some requests did not complete within timeout");
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for requests", e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Make an outbound TCP call to external service
     */
    private static Mono<String> makeTcpCall(String message) {
        logger.info("Making outbound TCP call to {}:{}", EXTERNAL_TCP_HOST, EXTERNAL_TCP_PORT);
        
        // Create HTTP request for httpbin.org
        String httpRequest = "GET /get?message=" + message + " HTTP/1.1\r\n" +
                           "Host: " + EXTERNAL_TCP_HOST + "\r\n" +
                           "Connection: close\r\n\r\n";
        
        return TcpClient.create()
                .host(EXTERNAL_TCP_HOST)
                .port(EXTERNAL_TCP_PORT)
                .connect()
                .flatMap(connection -> {
                    logger.info("Connected to external TCP server");
                    
                    return connection.outbound()
                            .sendString(Mono.just(httpRequest))
                            .then()
                            .then(connection.inbound()
                                    .receive()
                                    .asString(StandardCharsets.UTF_8)
                                    .collectList()
                                    .map(list -> String.join("", list)))
                            .doFinally(signalType -> {
                                logger.info("Closing TCP connection");
                                connection.dispose();
                            });
                })
                .doOnSuccess(data -> logger.info("Successfully received TCP response"))
                .doOnError(error -> logger.error("Error making outbound TCP call", error))
                .onErrorReturn("Error: " + message);
    }
    
    /**
     * Demonstrate advanced patterns
     */
    public static void demonstrateAdvancedPatterns() {
        logger.info("=== Advanced Reactor Netty TCP Patterns ===");
        
        // Pattern 1: Circuit breaker-like behavior
        logger.info("Pattern 1: Circuit breaker-like behavior");
        makeTcpCall("Circuit breaker test")
                .timeout(java.time.Duration.ofSeconds(1))
                .onErrorResume(throwable -> {
                    logger.warn("TCP request timed out, returning fallback response");
                    return Mono.just("{\"error\":\"timeout\",\"fallback\":true}");
                })
                .subscribe(
                        data -> logger.info("Circuit breaker response: {}", data),
                        error -> logger.error("Circuit breaker error", error)
                );
        
        // Pattern 2: Retry logic
        logger.info("Pattern 2: Retry logic");
        makeTcpCall("Retry test")
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
