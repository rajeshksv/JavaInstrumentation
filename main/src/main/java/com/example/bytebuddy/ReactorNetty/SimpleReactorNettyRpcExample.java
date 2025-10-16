package com.example.bytebuddy.ReactorNetty;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Simple Reactor Netty RPC Example that demonstrates:
 * 1. RPC Client making outbound RPC calls
 * 2. Reactive programming patterns with RPC
 */
public class SimpleReactorNettyRpcExample {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleReactorNettyRpcExample.class);
    private static final String RPC_SERVER_HOST = "localhost";
    private static final int RPC_SERVER_PORT = 8081;
    
    public static void main(String[] args) {
        logger.info("Starting Simple Reactor Netty RPC Example");
        
        // Demonstrate outbound RPC calls
        demonstrateOutboundRpcCalls();
        
        // Demonstrate advanced patterns
        demonstrateAdvancedPatterns();
        
        logger.info("Example completed");
    }
    
    /**
     * Demonstrate outbound RPC calls
     */
    private static void demonstrateOutboundRpcCalls() {
        logger.info("=== Outbound RPC Calls Demo ===");
        
        CountDownLatch latch = new CountDownLatch(3);
        
        // Simple RPC request
        logger.info("1. Simple RPC request - add method");
        makeRpcCall("add", 5, 3)
                .doOnSuccess(response -> logger.info("RPC add result: {}", response.getResult()))
                .doOnError(error -> logger.error("RPC add failed", error))
                .doFinally(signalType -> latch.countDown())
                .subscribe();
        
        // Multiple concurrent RPC requests
        logger.info("2. Multiple concurrent RPC requests");
        Mono.zip(
                makeRpcCall("multiply", 4, 7),
                makeRpcCall("echo", "Hello Reactor Netty!"),
                makeRpcCall("getTime")
        )
        .doOnSuccess(tuple -> {
            logger.info("Concurrent RPC results:");
            logger.info("  Multiply: {}", tuple.getT1().getResult());
            logger.info("  Echo: {}", tuple.getT2().getResult());
            logger.info("  Time: {}", tuple.getT3().getResult());
        })
        .doOnError(error -> logger.error("Concurrent RPC requests error", error))
        .doFinally(signalType -> latch.countDown())
        .subscribe();
        
        // Additional RPC request
        logger.info("3. Additional RPC request - getInfo");
        makeRpcCall("getInfo")
                .doOnSuccess(response -> logger.info("RPC getInfo result: {}", response.getResult()))
                .doOnError(error -> logger.error("RPC getInfo failed", error))
                .doFinally(signalType -> latch.countDown())
                .subscribe();
        
        try {
            // Wait for all requests to complete
            boolean completed = latch.await(30, TimeUnit.SECONDS);
            if (completed) {
                logger.info("All outbound RPC calls completed successfully!");
            } else {
                logger.warn("Some requests did not complete within timeout");
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for requests", e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Make an outbound RPC call
     */
    private static Mono<RpcResponse> makeRpcCall(String method, Object... params) {
        logger.info("Making outbound RPC call: method={}, params={}", method, Arrays.toString(params));
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RpcRequest request = new RpcRequest(java.util.UUID.randomUUID().toString(), method, params);
            String jsonRequest = objectMapper.writeValueAsString(request);
            
            return TcpClient.create()
                    .host(RPC_SERVER_HOST)
                    .port(RPC_SERVER_PORT)
                    .connect()
                    .flatMap(connection -> {
                        logger.info("Connected to RPC server");
                        
                        return connection.outbound()
                                .sendString(Mono.just(jsonRequest))
                                .then()
                                .then(connection.inbound()
                                        .receive()
                                        .asString(StandardCharsets.UTF_8)
                                        .next()
                                        .flatMap(responseJson -> {
                                            try {
                                                RpcResponse response = objectMapper.readValue(responseJson, RpcResponse.class);
                                                return Mono.just(response);
                                            } catch (Exception e) {
                                                logger.error("Error parsing RPC response", e);
                                                return Mono.error(e);
                                            }
                                        }))
                                .doFinally(signalType -> {
                                    logger.info("Closing RPC connection");
                                    connection.dispose();
                                });
                    })
                    .doOnSuccess(response -> logger.info("Successfully received RPC response"))
                    .doOnError(error -> logger.error("Error making outbound RPC call", error))
                    .onErrorReturn(new RpcResponse("error", "RPC call failed"));
                    
        } catch (Exception e) {
            logger.error("Error creating RPC request", e);
            return Mono.just(new RpcResponse("error", "Request creation failed: " + e.getMessage()));
        }
    }
    
    /**
     * Demonstrate advanced patterns
     */
    public static void demonstrateAdvancedPatterns() {
        logger.info("=== Advanced Reactor Netty RPC Patterns ===");
        
        // Pattern 1: Circuit breaker-like behavior
        logger.info("Pattern 1: Circuit breaker-like behavior");
        makeRpcCall("add", 10, 20)
                .timeout(java.time.Duration.ofSeconds(1))
                .onErrorResume(throwable -> {
                    logger.warn("RPC request timed out, returning fallback response");
                    return Mono.just(new RpcResponse("fallback", "Circuit breaker activated"));
                })
                .subscribe(
                        response -> logger.info("Circuit breaker response: {}", response.getResult()),
                        error -> logger.error("Circuit breaker error", error)
                );
        
        // Pattern 2: Retry logic
        logger.info("Pattern 2: Retry logic");
        makeRpcCall("multiply", 6, 8)
                .retry(3)
                .subscribe(
                        response -> logger.info("Retry successful: {}", response.getResult()),
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
