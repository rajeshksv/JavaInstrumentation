# VertxServer - Comprehensive Vert.x HTTP Server with Outbound Calls

A comprehensive demonstration of Vert.x HTTP server capabilities with outbound HTTP calls, showcasing key features and best practices.

## 🚀 Features Demonstrated

### Core Vert.x Features
- **HTTP Server** with routing and middleware
- **WebClient** for outbound HTTP calls
- **Async Programming** with CompletableFuture integration
- **Reactive Streams** with backpressure handling
- **Connection Pooling** and resource management
- **Error Handling** and circuit breaker patterns
- **Timeout Management** and retry logic

### Best Practices Implemented
- ✅ **Non-blocking I/O** throughout
- ✅ **Resource cleanup** on shutdown
- ✅ **Circuit breaker** for fault tolerance
- ✅ **Connection pooling** for performance
- ✅ **Comprehensive error handling**
- ✅ **Reactive patterns** with backpressure
- ✅ **Timeout and retry logic**
- ✅ **Health monitoring** endpoints

## 📋 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/health` | GET | Health check with metrics |
| `/simple-get` | GET | Simple HTTP GET to external service |
| `/async-get` | GET | Async GET with CompletableFuture |
| `/post-data` | POST | POST with JSON payload |
| `/concurrent` | GET | Multiple concurrent requests |
| `/circuit-breaker` | GET | Circuit breaker demonstration |
| `/error-demo` | GET | Error handling examples |
| `/reactive` | GET | Reactive streams demo |
| `/timeout` | GET | Timeout handling demo |

## 🛠️ Running the Server

### Start the VertxServer
```bash
cd main
mvn exec:java -Dexec.mainClass="com.example.bytebuddy.VertxServer.VertxServer"
```

### Run the Test Client
```bash
cd main
mvn exec:java -Dexec.mainClass="com.example.bytebuddy.VertxServer.VertxServerTestClient"
```

## 🧪 Testing the Server

### Manual Testing with curl

```bash
# Health check
curl http://localhost:8080/health

# Simple GET
curl http://localhost:8080/simple-get

# Async GET
curl http://localhost:8080/async-get

# POST with JSON
curl -X POST http://localhost:8080/post-data \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello VertxServer", "timestamp": 1234567890}'

# Concurrent requests
curl http://localhost:8080/concurrent

# Circuit breaker
curl http://localhost:8080/circuit-breaker

# Error handling
curl http://localhost:8080/error-demo?type=timeout

# Reactive streams
curl http://localhost:8080/reactive

# Timeout demo
curl http://localhost:8080/timeout
```

## 🏗️ Architecture

### Key Components

1. **VertxServer** - Main HTTP server with routing
2. **WebClient** - Outbound HTTP client with pooling
3. **Circuit Breaker** - Fault tolerance mechanism
4. **Error Handler** - Comprehensive error handling
5. **Test Client** - Automated testing suite

### Design Patterns

- **Reactor Pattern** - Event-driven architecture
- **Circuit Breaker** - Fault tolerance
- **Connection Pooling** - Resource optimization
- **Reactive Streams** - Backpressure handling
- **Async/Await** - Non-blocking operations

## 🔧 Configuration

### WebClient Configuration
```java
WebClientOptions options = new WebClientOptions()
    .setKeepAlive(true)           // Keep connections alive
    .setMaxPoolSize(20)           // Connection pool size
    .setIdleTimeout(30)           // Idle timeout in seconds
    .setConnectTimeout(5000)      // Connection timeout
    .setDefaultHost("httpbin.org") // Default host
    .setDefaultPort(443)          // Default port
    .setSsl(true);                // Enable SSL
```

### Circuit Breaker Configuration
```java
private static final int MAX_FAILURES = 5;        // Max failures before opening
private static final long CIRCUIT_TIMEOUT = 30000; // Circuit reset timeout
```

## 📊 Monitoring and Metrics

### Health Check Response
```json
{
  "status": "UP",
  "timestamp": 1234567890,
  "requests": 42,
  "circuitOpen": false
}
```

### Request Metrics
- Request counter
- Circuit breaker status
- Response times
- Error rates

## 🚨 Error Handling

### Error Types Handled
- **Connection timeouts**
- **HTTP errors (4xx, 5xx)**
- **Network failures**
- **Circuit breaker states**
- **Resource exhaustion**

### Error Response Format
```json
{
  "status": "error",
  "message": "Connection timeout",
  "timestamp": 1234567890,
  "requestId": 42
}
```

## 🔄 Async Patterns

### CompletableFuture Integration
```java
CompletableFuture<JsonObject> future = new CompletableFuture<>();

webClient.get("/json").send()
    .onSuccess(response -> future.complete(result))
    .onFailure(throwable -> future.completeExceptionally(throwable));

Future.fromCompletionStage(future)
    .onSuccess(result -> ctx.response().end(result.encode()))
    .onFailure(throwable -> handleError(ctx, throwable));
```

### Reactive Streams
```java
response.handler(buffer -> {
    // Process each chunk as it arrives
    System.out.println("Received chunk: " + buffer.length() + " bytes");
});
```

## 🛡️ Circuit Breaker Implementation

### States
1. **CLOSED** - Normal operation
2. **OPEN** - Circuit is open, requests fail fast
3. **HALF_OPEN** - Testing if service is back

### Configuration
- **Max Failures**: 5 consecutive failures
- **Timeout**: 30 seconds before reset
- **Failure Threshold**: Configurable

## 📈 Performance Features

### Connection Pooling
- **Max Pool Size**: 20 connections
- **Keep Alive**: Enabled
- **Idle Timeout**: 30 seconds

### Timeout Management
- **Connect Timeout**: 5 seconds
- **Request Timeout**: Configurable per request
- **Idle Timeout**: 30 seconds

### Backpressure Handling
- **Reactive Streams**: Handle backpressure automatically
- **Buffer Management**: Efficient memory usage
- **Flow Control**: Prevent overwhelming downstream services

## 🔍 Debugging and Logging

### Log Levels
- **INFO**: Normal operations
- **WARN**: Circuit breaker state changes
- **ERROR**: Failures and exceptions

### Debug Information
- Request/response logging
- Timing information
- Error details
- Circuit breaker state

## 🚀 Production Considerations

### Security
- SSL/TLS support
- Input validation
- Rate limiting (can be added)

### Monitoring
- Health check endpoints
- Metrics collection
- Circuit breaker monitoring

### Scalability
- Connection pooling
- Resource management
- Async processing

## 📚 Learning Outcomes

After studying this code, you'll understand:

1. **Vert.x HTTP Server** setup and configuration
2. **WebClient** usage for outbound calls
3. **Async programming** patterns
4. **Circuit breaker** implementation
5. **Error handling** strategies
6. **Reactive streams** processing
7. **Connection pooling** optimization
8. **Timeout management**
9. **Resource cleanup** patterns
10. **Testing strategies** for async code

## 🔗 External Dependencies

The server makes calls to `httpbin.org` for testing:
- `/get` - Simple GET requests
- `/post` - POST requests
- `/json` - JSON responses
- `/delay/{seconds}` - Delayed responses
- `/status/{code}` - HTTP status codes
- `/stream/{count}` - Streaming responses

## 📝 Notes

- The server uses `httpbin.org` as the external service for demonstrations
- All endpoints are designed to be educational and demonstrate best practices
- The circuit breaker is simplified for demonstration purposes
- In production, you'd want more sophisticated monitoring and alerting
