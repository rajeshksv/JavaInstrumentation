package com.example.bytebuddy.VertxServer;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Test client for VertxServer demonstrating various HTTP client patterns
 * and testing all the server endpoints.
 */
public class VertxServerTestClient {

    private final Vertx vertx;
    private final WebClient webClient;

    public VertxServerTestClient() {
        this.vertx = Vertx.vertx();
        
        WebClientOptions options = new WebClientOptions()
                .setKeepAlive(true)
                .setMaxPoolSize(10)
                .setConnectTimeout(5000);
        
        this.webClient = WebClient.create(vertx, options);
    }

    public void runAllTests() {
        System.out.println("🧪 Starting VertxServer Test Suite...\n");
        
        // Run tests sequentially to demonstrate different patterns
        testHealthCheck()
            .thenCompose(v -> testSimpleGet())
            .thenCompose(v -> testAsyncGet())
            .thenCompose(v -> testPostData())
            .thenCompose(v -> testConcurrent())
            .thenCompose(v -> testCircuitBreaker())
            .thenCompose(v -> testErrorHandling())
            .thenCompose(v -> testReactive())
            .thenCompose(v -> testTimeout())
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    System.out.println("❌ Test suite failed: " + throwable.getMessage());
                } else {
                    System.out.println("✅ All tests completed successfully!");
                }
                shutdown();
            });
    }

    private CompletableFuture<Void> testHealthCheck() {
        System.out.println("🔍 Testing Health Check...");
        
        return webClient.get(8080, "localhost", "/health")
                .send()
                .toCompletionStage()
                .toCompletableFuture()
                .thenAccept(response -> {
                    System.out.println("✅ Health Check Response: " + response.statusCode());
                    JsonObject body = response.bodyAsJsonObject();
                    System.out.println("   Status: " + body.getString("status"));
                    System.out.println("   Requests: " + body.getInteger("requests"));
                    System.out.println("   Circuit Open: " + body.getBoolean("circuitOpen"));
                })
                .exceptionally(throwable -> {
                    System.out.println("❌ Health Check failed: " + throwable.getMessage());
                    return null;
                });
    }

    private CompletableFuture<Void> testSimpleGet() {
        System.out.println("\n📡 Testing Simple GET...");
        
        return webClient.get(8080, "localhost", "/simple-get")
                .send()
                .toCompletionStage()
                .toCompletableFuture()
                .thenAccept(response -> {
                    System.out.println("✅ Simple GET Response: " + response.statusCode());
                    JsonObject body = response.bodyAsJsonObject();
                    System.out.println("   Status: " + body.getString("status"));
                    System.out.println("   External Status: " + body.getInteger("externalStatusCode"));
                    System.out.println("   Response Size: " + body.getInteger("responseSize"));
                })
                .exceptionally(throwable -> {
                    System.out.println("❌ Simple GET failed: " + throwable.getMessage());
                    return null;
                });
    }

    private CompletableFuture<Void> testAsyncGet() {
        System.out.println("\n🔄 Testing Async GET...");
        
        return webClient.get(8080, "localhost", "/async-get")
                .send()
                .toCompletionStage()
                .toCompletableFuture()
                .thenAccept(response -> {
                    System.out.println("✅ Async GET Response: " + response.statusCode());
                    JsonObject body = response.bodyAsJsonObject();
                    System.out.println("   Status: " + body.getString("status"));
                    System.out.println("   Data Keys: " + body.getJsonObject("data").fieldNames());
                })
                .exceptionally(throwable -> {
                    System.out.println("❌ Async GET failed: " + throwable.getMessage());
                    return null;
                });
    }

    private CompletableFuture<Void> testPostData() {
        System.out.println("\n📤 Testing POST Data...");
        
        JsonObject requestData = new JsonObject()
                .put("message", "Hello from Test Client")
                .put("timestamp", System.currentTimeMillis())
                .put("testId", "test-" + System.nanoTime());
        
        return webClient.post(8080, "localhost", "/post-data")
                .sendJsonObject(requestData)
                .toCompletionStage()
                .toCompletableFuture()
                .thenAccept(response -> {
                    System.out.println("✅ POST Data Response: " + response.statusCode());
                    JsonObject body = response.bodyAsJsonObject();
                    System.out.println("   Status: " + body.getString("status"));
                    System.out.println("   Sent Data: " + body.getJsonObject("sentData").getString("message"));
                })
                .exceptionally(throwable -> {
                    System.out.println("❌ POST Data failed: " + throwable.getMessage());
                    return null;
                });
    }

    private CompletableFuture<Void> testConcurrent() {
        System.out.println("\n⚡ Testing Concurrent Requests...");
        
        long startTime = System.currentTimeMillis();
        
        return webClient.get(8080, "localhost", "/concurrent")
                .send()
                .toCompletionStage()
                .toCompletableFuture()
                .thenAccept(response -> {
                    long duration = System.currentTimeMillis() - startTime;
                    System.out.println("✅ Concurrent Response: " + response.statusCode());
                    JsonObject body = response.bodyAsJsonObject();
                    System.out.println("   Status: " + body.getString("status"));
                    System.out.println("   Concurrent Requests: " + body.getInteger("concurrentRequests"));
                    System.out.println("   Duration: " + duration + "ms");
                })
                .exceptionally(throwable -> {
                    System.out.println("❌ Concurrent test failed: " + throwable.getMessage());
                    return null;
                });
    }

    private CompletableFuture<Void> testCircuitBreaker() {
        System.out.println("\n🔌 Testing Circuit Breaker...");
        
        return webClient.get(8080, "localhost", "/circuit-breaker")
                .send()
                .toCompletionStage()
                .toCompletableFuture()
                .thenAccept(response -> {
                    System.out.println("✅ Circuit Breaker Response: " + response.statusCode());
                    JsonObject body = response.bodyAsJsonObject();
                    System.out.println("   Status: " + body.getString("status"));
                    System.out.println("   Message: " + body.getString("message"));
                    if (body.containsKey("failures")) {
                        System.out.println("   Failures: " + body.getInteger("failures"));
                    }
                })
                .exceptionally(throwable -> {
                    System.out.println("❌ Circuit Breaker test failed: " + throwable.getMessage());
                    return null;
                });
    }

    private CompletableFuture<Void> testErrorHandling() {
        System.out.println("\n💥 Testing Error Handling...");
        
        return webClient.get(8080, "localhost", "/error-demo?type=timeout")
                .send()
                .toCompletionStage()
                .toCompletableFuture()
                .thenAccept(response -> {
                    System.out.println("✅ Error Handling Response: " + response.statusCode());
                    JsonObject body = response.bodyAsJsonObject();
                    System.out.println("   Status: " + body.getString("status"));
                    System.out.println("   Message: " + body.getString("message"));
                })
                .exceptionally(throwable -> {
                    System.out.println("❌ Error Handling test failed: " + throwable.getMessage());
                    return null;
                });
    }

    private CompletableFuture<Void> testReactive() {
        System.out.println("\n🌊 Testing Reactive Streams...");
        
        return webClient.get(8080, "localhost", "/reactive")
                .send()
                .toCompletionStage()
                .toCompletableFuture()
                .thenAccept(response -> {
                    System.out.println("✅ Reactive Response: " + response.statusCode());
                    JsonObject body = response.bodyAsJsonObject();
                    System.out.println("   Status: " + body.getString("status"));
                    System.out.println("   Message: " + body.getString("message"));
                })
                .exceptionally(throwable -> {
                    System.out.println("❌ Reactive test failed: " + throwable.getMessage());
                    return null;
                });
    }

    private CompletableFuture<Void> testTimeout() {
        System.out.println("\n⏰ Testing Timeout...");
        
        return webClient.get(8080, "localhost", "/timeout")
                .send()
                .toCompletionStage()
                .toCompletableFuture()
                .thenAccept(response -> {
                    System.out.println("✅ Timeout Response: " + response.statusCode());
                    JsonObject body = response.bodyAsJsonObject();
                    System.out.println("   Status: " + body.getString("status"));
                    System.out.println("   Message: " + body.getString("message"));
                })
                .exceptionally(throwable -> {
                    System.out.println("❌ Timeout test failed: " + throwable.getMessage());
                    return null;
                });
    }

    private void shutdown() {
        System.out.println("\n🛑 Shutting down test client...");
        webClient.close();
        vertx.close();
    }

    public static void main(String[] args) {
        System.out.println("🚀 Starting VertxServer Test Client...");
        
        VertxServerTestClient client = new VertxServerTestClient();
        
        // Wait a bit for server to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        client.runAllTests();
    }
}
