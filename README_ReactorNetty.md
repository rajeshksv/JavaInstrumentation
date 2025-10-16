# Reactor Netty Examples

This directory contains comprehensive examples of Reactor Netty server and client implementations that demonstrate outbound HTTP calls.

## Overview

Reactor Netty is a reactive HTTP client and server library built on top of Netty and Project Reactor. It provides a non-blocking, reactive programming model for building high-performance HTTP applications.

## Examples Included

### 1. SimpleReactorNettyServer.java
A Reactor Netty HTTP server that:
- Accepts HTTP requests on port 8080
- Makes outbound HTTP calls to external services
- Provides health check endpoint
- Includes error handling and logging

**Key Features:**
- Health check endpoint: `GET /health`
- External data endpoint: `GET /api/external-data` (makes outbound call to JSONPlaceholder API)
- Echo endpoint: `POST /api/echo`

### 2. SimpleReactorNettyClient.java
A Reactor Netty HTTP client that:
- Makes GET and POST requests
- Supports concurrent requests
- Includes retry logic
- Demonstrates error handling

**Key Features:**
- Simple GET requests
- POST requests with JSON body
- Multiple concurrent requests
- Retry mechanism with configurable attempts

### 3. SimpleReactorNettyExample.java
A comprehensive example that:
- Starts the Reactor Netty server
- Uses the client to test server endpoints
- Demonstrates server making outbound calls
- Shows advanced patterns like circuit breaker behavior

## Running the Examples

### Prerequisites
- Java 8 or higher
- Maven 3.6 or higher

### Build the Project
```bash
mvn clean compile
```

### Run the Server
```bash
mvn exec:java -Dexec.mainClass="com.example.bytebuddy.ReactorNetty.SimpleReactorNettyServer"
```

### Run the Client
```bash
mvn exec:java -Dexec.mainClass="com.example.bytebuddy.ReactorNetty.SimpleReactorNettyClient"
```

### Run the Complete Example
```bash
mvn exec:java -Dexec.mainClass="com.example.bytebuddy.ReactorNetty.SimpleReactorNettyExample"
```

## Testing the Server

Once the server is running, you can test it using curl:

### Health Check
```bash
curl http://localhost:8080/health
```

### External Data (Server makes outbound call)
```bash
curl http://localhost:8080/api/external-data
```

### Echo Endpoint
```bash
curl -X POST http://localhost:8080/api/echo \
  -H "Content-Type: application/json" \
  -d '{"message":"Hello from Reactor Netty!"}'
```

## Key Reactor Netty Concepts Demonstrated

### 1. Reactive Programming
- Uses `Mono` and `Flux` for reactive streams
- Non-blocking I/O operations
- Backpressure handling

### 2. HTTP Server
- Route-based request handling
- Response composition
- Error handling with `onErrorResume`

### 3. HTTP Client
- Outbound HTTP calls
- Response transformation
- Concurrent request handling

### 4. Advanced Patterns
- Circuit breaker pattern
- Request batching
- Retry logic
- Timeout handling

## Dependencies

The examples use the following key dependencies:

```xml
<!-- Reactor Netty -->
<dependency>
    <groupId>io.projectreactor.netty</groupId>
    <artifactId>reactor-netty-http</artifactId>
    <version>1.1.15</version>
</dependency>

<!-- Reactor Netty Core -->
<dependency>
    <groupId>io.projectreactor.netty</groupId>
    <artifactId>reactor-netty-core</artifactId>
    <version>1.1.15</version>
</dependency>

<!-- Project Reactor Core -->
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.6.3</version>
</dependency>
```

## Architecture

```
┌─────────────────┐    HTTP Request    ┌──────────────────┐
│   HTTP Client   │ ──────────────────► │  Reactor Netty   │
│                 │                    │     Server       │
└─────────────────┘                    └──────────────────┘
                                                │
                                                │ Outbound HTTP Call
                                                ▼
                                       ┌──────────────────┐
                                       │  External API    │
                                       │  (JSONPlaceholder)│
                                       └──────────────────┘
```

## Benefits of Reactor Netty

1. **High Performance**: Built on Netty for excellent performance
2. **Reactive**: Non-blocking, backpressure-aware
3. **Lightweight**: Minimal resource usage
4. **Flexible**: Easy to configure and extend
5. **Production Ready**: Used in Spring WebFlux and other frameworks

## Use Cases

- Microservices communication
- API gateways
- Proxy servers
- High-throughput HTTP clients
- Reactive web applications

## Best Practices Demonstrated

1. **Error Handling**: Proper error handling with fallbacks
2. **Logging**: Comprehensive logging for debugging
3. **Resource Management**: Proper cleanup of resources
4. **Timeout Handling**: Configurable timeouts
5. **Retry Logic**: Exponential backoff and retry mechanisms
