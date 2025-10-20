package com.example.bytebuddy.VertxServer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.buffer.Buffer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * VertxServer demonstrating key Vert.x features and best practices:
 * - HTTP Server with routing
 * - Outbound HTTP calls with WebClient
 * - Async patterns with CompletableFuture
 * - Error handling and circuit breaker
 * - Reactive streams and backpressure
 * - Connection pooling and timeouts
 */
public class VertxServer extends AbstractVerticle {

    private WebClient webClient;
    private HttpServer server;
    private final AtomicInteger requestCounter = new AtomicInteger(0);
    private final AtomicInteger circuitBreakerFailures = new AtomicInteger(0);
    private volatile boolean circuitOpen = false;
    private static final int MAX_FAILURES = 5;
    private static final long CIRCUIT_TIMEOUT = 30000; // 30 seconds

    @Override
    public void start(Promise<Void> startPromise) {
        System.out.println("🚀 Starting VertxServer...");
        
        // Configure WebClient with connection pooling and timeouts
        WebClientOptions options = new WebClientOptions()
                .setKeepAlive(true)
                .setMaxPoolSize(20)
                .setIdleTimeout(30)
                .setConnectTimeout(5000)
                .setDefaultHost("httpbin.org")
                .setDefaultPort(443)
                .setSsl(true);

        webClient = WebClient.create(vertx, options);
        
        // Create HTTP server
        server = vertx.createHttpServer();
        
        // Setup routing
        Router router = setupRoutes();
        
        // Start server
        server.requestHandler(router)
              .listen(8080)
              .onComplete(result -> {
                  if (result.succeeded()) {
                      System.out.println("✅ VertxServer started on port 8080");
                      startPromise.complete();
                  } else {
                      System.out.println("❌ Failed to start server: " + result.cause());
                      startPromise.fail(result.cause());
                  }
              });
    }

    private Router setupRoutes() {
        Router router = Router.router(vertx);
        
        // Health check endpoint
        router.get("/health").handler(this::handleHealth);
        
        // Simple GET request
        router.get("/simple-get").handler(this::handleSimpleGet);
        
        // Async GET with CompletableFuture
        router.get("/async-get").handler(this::handleAsyncGet);
        
        // POST request with JSON
        router.post("/post-data").handler(this::handlePostData);
        
        // Multiple concurrent requests
        router.get("/concurrent").handler(this::handleConcurrent);
        
        // Circuit breaker demo
        router.get("/circuit-breaker").handler(this::handleCircuitBreaker);
        
        // Error handling demo
        router.get("/error-demo").handler(this::handleErrorDemo);
        
        // Reactive streams demo
        router.get("/reactive").handler(this::handleReactive);
        
        // Timeout demo
        router.get("/timeout").handler(this::handleTimeout);
        
        return router;
    }

    private void handleHealth(RoutingContext ctx) {
        JsonObject health = new JsonObject()
                .put("status", "UP")
                .put("timestamp", System.currentTimeMillis())
                .put("requests", requestCounter.get())
                .put("circuitOpen", circuitOpen);
        
        ctx.response()
           .putHeader("Content-Type", "application/json")
           .end(health.encode());
    }

    private void handleSimpleGet(RoutingContext ctx) {
        System.out.println("📡 Handling simple GET request");
        
        webClient.get("/get")
                 .addQueryParam("param1", "value1")
                 .addQueryParam("param2", "value2")
                 .send()
                 .onSuccess(response -> {
                     System.out.println("✅ Simple GET successful: " + response.statusCode());
                     
                     JsonObject result = new JsonObject()
                             .put("status", "success")
                             .put("externalStatusCode", response.statusCode())
                             .put("responseSize", response.bodyAsString().length())
                             .put("timestamp", System.currentTimeMillis());
                     
                     ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(result.encode());
                 })
                 .onFailure(throwable -> {
                     System.out.println("❌ Simple GET failed: " + throwable.getMessage());
                     handleError(ctx, throwable);
                 });
    }

    private void handleAsyncGet(RoutingContext ctx) {
        System.out.println("🔄 Handling async GET request");
        
        // Demonstrate CompletableFuture integration
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        
        webClient.get("/json")
                 .send()
                 .onSuccess(response -> {
                     JsonObject data = response.bodyAsJsonObject();
                     JsonObject result = new JsonObject()
                             .put("status", "success")
                             .put("data", data)
                             .put("processingTime", System.currentTimeMillis());
                     future.complete(result);
                 })
                 .onFailure(throwable -> {
                     future.completeExceptionally(throwable);
                 });
        
        // Convert CompletableFuture to Vert.x Future
        Future.fromCompletionStage(future)
              .onSuccess(result -> {
                  ctx.response()
                     .putHeader("Content-Type", "application/json")
                     .end(result.encode());
              })
              .onFailure(throwable -> {
                  handleError(ctx, throwable);
              });
    }

    private void handlePostData(RoutingContext ctx) {
        System.out.println("📤 Handling POST request");
        
        JsonObject requestData = ctx.getBodyAsJson();
        if (requestData == null) {
            requestData = new JsonObject().put("message", "Hello from VertxServer");
        }
        
        final JsonObject finalRequestData = requestData;
        
        webClient.post("/post")
                 .putHeader("Content-Type", "application/json")
                 .sendJsonObject(finalRequestData)
                 .onSuccess(response -> {
                     System.out.println("✅ POST successful: " + response.statusCode());
                     
                     JsonObject result = new JsonObject()
                             .put("status", "success")
                             .put("sentData", finalRequestData)
                             .put("response", response.bodyAsJsonObject())
                             .put("timestamp", System.currentTimeMillis());
                     
                     ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(result.encode());
                 })
                 .onFailure(throwable -> {
                     System.out.println("❌ POST failed: " + throwable.getMessage());
                     handleError(ctx, throwable);
                 });
    }

    private void handleConcurrent(RoutingContext ctx) {
        System.out.println("⚡ Handling concurrent requests");
        
        // Make multiple concurrent requests
        Future<HttpResponse<Buffer>> request1 = webClient.get("/delay/1").send();
        Future<HttpResponse<Buffer>> request2 = webClient.get("/delay/2").send();
        Future<HttpResponse<Buffer>> request3 = webClient.get("/delay/1").send();
        
        // Wait for all requests to complete
        Future.all(request1, request2, request3)
              .onSuccess(compositeFuture -> {
                  HttpResponse<Buffer> resp1 = request1.result();
                  HttpResponse<Buffer> resp2 = request2.result();
                  HttpResponse<Buffer> resp3 = request3.result();
                  
                  JsonObject result = new JsonObject()
                          .put("status", "success")
                          .put("concurrentRequests", 3)
                          .put("results", new JsonObject()
                                  .put("request1", resp1.statusCode())
                                  .put("request2", resp2.statusCode())
                                  .put("request3", resp3.statusCode()))
                          .put("totalTime", System.currentTimeMillis());
                  
                  ctx.response()
                     .putHeader("Content-Type", "application/json")
                     .end(result.encode());
              })
              .onFailure(throwable -> {
                  handleError(ctx, throwable);
              });
    }

    private void handleCircuitBreaker(RoutingContext ctx) {
        System.out.println("🔌 Handling circuit breaker demo");
        
        if (circuitOpen) {
            // Check if circuit should be reset
            if (System.currentTimeMillis() - getLastFailureTime() > CIRCUIT_TIMEOUT) {
                circuitOpen = false;
                circuitBreakerFailures.set(0);
                System.out.println("🔄 Circuit breaker reset");
            }
        }
        
        if (circuitOpen) {
            JsonObject result = new JsonObject()
                    .put("status", "circuit_open")
                    .put("message", "Circuit breaker is open")
                    .put("failures", circuitBreakerFailures.get());
            
            ctx.response()
               .putHeader("Content-Type", "application/json")
               .end(result.encode());
            return;
        }
        
        // Make request to a potentially failing endpoint
        webClient.get("/status/500") // This will return 500
                 .send()
                 .onSuccess(response -> {
                     if (response.statusCode() >= 400) {
                         handleCircuitBreakerFailure();
                     } else {
                         circuitBreakerFailures.set(0);
                         
                         JsonObject result = new JsonObject()
                                 .put("status", "success")
                                 .put("circuitBreaker", "closed")
                                 .put("failures", circuitBreakerFailures.get());
                         
                         ctx.response()
                            .putHeader("Content-Type", "application/json")
                            .end(result.encode());
                     }
                 })
                 .onFailure(throwable -> {
                     handleCircuitBreakerFailure();
                     handleError(ctx, throwable);
                 });
    }

    private void handleErrorDemo(RoutingContext ctx) {
        System.out.println("💥 Handling error demo");
        
        // Demonstrate different types of errors
        String errorType = ctx.request().getParam("type");
        
        if ("timeout".equals(errorType)) {
            webClient.get("/delay/10") // This will timeout
                     .send()
                     .onSuccess(response -> {
                         ctx.response().end("Unexpected success");
                     })
                     .onFailure(throwable -> {
                         handleError(ctx, throwable);
                     });
        } else if ("notfound".equals(errorType)) {
            webClient.get("/nonexistent")
                     .send()
                     .onSuccess(response -> {
                         ctx.response().end("Unexpected success");
                     })
                     .onFailure(throwable -> {
                         handleError(ctx, throwable);
                     });
        } else {
            // Default error
            ctx.fail(500);
        }
    }

    private void handleReactive(RoutingContext ctx) {
        System.out.println("🌊 Handling reactive streams demo");
        
        // Demonstrate reactive patterns with backpressure
        vertx.createHttpClient()
             .request(HttpMethod.GET, 443, "httpbin.org", "/stream/10")
             .onSuccess(request -> {
                 request.send()
                        .onSuccess(response -> {
                            response.handler(buffer -> {
                                // Process each chunk as it arrives
                                System.out.println("📦 Received chunk: " + buffer.length() + " bytes");
                            });
                            
                            response.endHandler(v -> {
                                JsonObject result = new JsonObject()
                                        .put("status", "success")
                                        .put("message", "Reactive stream completed")
                                        .put("timestamp", System.currentTimeMillis());
                                
                                ctx.response()
                                   .putHeader("Content-Type", "application/json")
                                   .end(result.encode());
                            });
                        })
                        .onFailure(throwable -> {
                            handleError(ctx, throwable);
                        });
             })
             .onFailure(throwable -> {
                 handleError(ctx, throwable);
             });
    }

    private void handleTimeout(RoutingContext ctx) {
        System.out.println("⏰ Handling timeout demo");
        
        // Set a short timeout for demonstration
        webClient.get("/delay/5")
                 .timeout(2000) // 2 second timeout
                 .send()
                 .onSuccess(response -> {
                     ctx.response().end("Request completed successfully");
                 })
                 .onFailure(throwable -> {
                     if (throwable.getMessage().contains("timeout")) {
                         JsonObject result = new JsonObject()
                                 .put("status", "timeout")
                                 .put("message", "Request timed out after 2 seconds")
                                 .put("timestamp", System.currentTimeMillis());
                         
                         ctx.response()
                            .putHeader("Content-Type", "application/json")
                            .end(result.encode());
                     } else {
                         handleError(ctx, throwable);
                     }
                 });
    }

    private void handleCircuitBreakerFailure() {
        int failures = circuitBreakerFailures.incrementAndGet();
        System.out.println("💥 Circuit breaker failure #" + failures);
        
        if (failures >= MAX_FAILURES) {
            circuitOpen = true;
            System.out.println("🔌 Circuit breaker opened");
        }
    }

    private long getLastFailureTime() {
        // In a real implementation, you'd track this properly
        return System.currentTimeMillis() - 10000; // Mock implementation
    }

    private void handleError(RoutingContext ctx, Throwable throwable) {
        System.out.println("❌ Error occurred: " + throwable.getMessage());
        
        JsonObject error = new JsonObject()
                .put("status", "error")
                .put("message", throwable.getMessage())
                .put("timestamp", System.currentTimeMillis())
                .put("requestId", requestCounter.incrementAndGet());
        
        ctx.response()
           .setStatusCode(500)
           .putHeader("Content-Type", "application/json")
           .end(error.encode());
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        System.out.println("🛑 Stopping VertxServer...");
        
        if (webClient != null) {
            webClient.close();
        }
        
        if (server != null) {
            server.close()
                  .onComplete(result -> {
                      if (result.succeeded()) {
                          System.out.println("✅ VertxServer stopped successfully");
                          stopPromise.complete();
                      } else {
                          System.out.println("❌ Error stopping server: " + result.cause());
                          stopPromise.fail(result.cause());
                      }
                  });
        } else {
            stopPromise.complete();
        }
    }

    public static void main(String[] args) {
        System.out.println("🚀 Starting VertxServer Application...");
        
        Vertx vertx = Vertx.vertx();
        VertxServer server = new VertxServer();
        
        vertx.deployVerticle(server)
             .onSuccess(id -> {
                 System.out.println("✅ VertxServer deployed with ID: " + id);
                 System.out.println("🌐 Server available at: http://localhost:8080");
                 System.out.println("📋 Available endpoints:");
                 System.out.println("   GET  /health - Health check");
                 System.out.println("   GET  /simple-get - Simple HTTP GET");
                 System.out.println("   GET  /async-get - Async GET with CompletableFuture");
                 System.out.println("   POST /post-data - POST with JSON");
                 System.out.println("   GET  /concurrent - Multiple concurrent requests");
                 System.out.println("   GET  /circuit-breaker - Circuit breaker demo");
                 System.out.println("   GET  /error-demo?type=timeout - Error handling demo");
                 System.out.println("   GET  /reactive - Reactive streams demo");
                 System.out.println("   GET  /timeout - Timeout demo");
             })
             .onFailure(throwable -> {
                 System.out.println("❌ Failed to deploy VertxServer: " + throwable.getMessage());
                 System.exit(1);
             });
        
        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🛑 Shutting down VertxServer...");
            vertx.close();
        }));
    }
}
