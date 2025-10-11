# Netty RPC Server with Outbound RPC Calls

This project demonstrates a Netty server that receives incoming RPC requests and makes outbound RPC calls to external services.

## Architecture

The system consists of several components:

1. **RpcServer** - Main server that receives requests and makes outbound RPC calls
2. **RpcClient** - Client for making outbound RPC calls to external services
3. **SimpleRpcService** - External RPC service that the main server calls
4. **TestClient** - Test client to demonstrate the functionality
5. **RPC Protocol Classes** - Message classes for RPC communication

## Components

### RPC Protocol Classes
- `RpcMessage` - Base class for all RPC messages
- `RpcRequest` - Request message with method name and parameters
- `RpcResponse` - Response message with result or error

### Netty Server (`RpcServer`)
- Listens on port 8080
- Receives incoming RPC requests
- Makes outbound RPC calls to external service
- Returns processed responses to clients

### Netty Client (`RpcClient`)
- Connects to external RPC service
- Sends RPC requests asynchronously
- Handles responses and errors

### Simple RPC Service (`SimpleRpcService`)
- External service listening on port 8081
- Provides simple business logic methods:
  - `processData` - Processes data from main server
  - `calculate` - Performs arithmetic calculations
  - `echo` - Echoes back the input message

### Test Client (`TestClient`)
- Connects to the main server
- Sends test requests
- Displays responses

## How to Run

### 1. Start the Main Application
```bash
mvn compile exec:java -Dexec.mainClass="org.example.Netty.NettyRpcApplication"
```

This will start:
- Simple RPC Service on port 8081
- Main RPC Server on port 8080
- The main server will connect to the RPC service

### 2. Test with the Test Client
In another terminal:
```bash
mvn compile exec:java -Dexec.mainClass="org.example.Netty.TestClient"
```

### 3. Available Test Commands
Once the test client is connected, you can use:
- `echo <message>` - Send an echo request
- `calculate <number1> <number2>` - Send a calculation request
- `quit` - Exit the client

## Example Flow

1. Test client sends request to main server (port 8080)
2. Main server receives request and makes outbound RPC call to simple service (port 8081)
3. Simple service processes the request and returns response
4. Main server receives response and forwards it back to test client
5. Test client displays the final result

## Features

- **Asynchronous Processing**: All RPC calls are handled asynchronously
- **Message Framing**: Uses length-prefixed messages for reliable communication
- **Error Handling**: Comprehensive error handling and reporting
- **JSON Serialization**: Uses Jackson for message serialization
- **Connection Management**: Proper connection lifecycle management
- **Interactive Testing**: Command-line interface for testing

## Dependencies

- Netty 4.1.100.Final - For network communication
- Jackson 2.15.2 - For JSON serialization
- Java 8+ - Runtime requirement

## Configuration

You can modify the ports and host settings in `NettyRpcApplication.java`:
- `MAIN_SERVER_PORT` - Port for the main server (default: 8080)
- `RPC_SERVICE_PORT` - Port for the RPC service (default: 8081)
- `RPC_SERVICE_HOST` - Host for the RPC service (default: localhost)

