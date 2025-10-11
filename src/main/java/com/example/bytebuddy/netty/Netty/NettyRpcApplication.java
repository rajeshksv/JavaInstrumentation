package com.example.bytebuddy.netty.Netty;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

/**
 * Main application demonstrating Netty server with outbound RPC calls
 */
public class NettyRpcApplication {
    private static final int MAIN_SERVER_PORT = 8080;
    private static final int RPC_SERVICE_PORT = 8081;
    private static final String RPC_SERVICE_HOST = "localhost";
    
    private RpcServer mainServer;
    private SimpleRpcService rpcService;
    
    public static void main(String[] args) {
        NettyRpcApplication app = new NettyRpcApplication();
        app.start();
    }
    
    public void start() {
        System.out.println("Starting Netty RPC Application...");
        
        // Start the simple RPC service first
        rpcService = new SimpleRpcService(RPC_SERVICE_PORT);
        rpcService.start()
            .thenCompose(v -> {
                System.out.println("RPC Service started successfully");
                
                // Start the main server that will make outbound calls
                mainServer = new RpcServer(MAIN_SERVER_PORT, RPC_SERVICE_HOST, RPC_SERVICE_PORT);
                return mainServer.start();
            })
            .thenCompose(v -> {
                System.out.println("Main server started successfully");
                
                // Connect to the RPC service
                return mainServer.connectToRpcServer();
            })
            .thenRun(() -> {
                System.out.println("Netty RPC Application is ready!");
                System.out.println("Main server listening on port " + MAIN_SERVER_PORT);
                System.out.println("RPC service listening on port " + RPC_SERVICE_PORT);
                System.out.println("Type 'quit' to exit");
                
                // Start interactive mode
                startInteractiveMode();
            })
            .exceptionally(throwable -> {
                System.err.println("Failed to start application: " + throwable.getMessage());
                throwable.printStackTrace();
                return null;
            });
    }
    
    private void startInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down...");
            stop();
        }));
        
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            
            if ("quit".equalsIgnoreCase(input)) {
                break;
            } else if ("status".equalsIgnoreCase(input)) {
                System.out.println("Application Status:");
                System.out.println("- Main server: " + (mainServer != null ? "Running" : "Stopped"));
                System.out.println("- RPC service: " + (rpcService != null ? "Running" : "Stopped"));
            } else if ("help".equalsIgnoreCase(input)) {
                printHelp();
            } else if (!input.isEmpty()) {
                System.out.println("Unknown command: " + input + ". Type 'help' for available commands.");
            }
        }
        
        scanner.close();
        stop();
    }
    
    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  status  - Show application status");
        System.out.println("  help    - Show this help message");
        System.out.println("  quit    - Exit the application");
    }
    
    public void stop() {
        if (mainServer != null) {
            mainServer.stop();
        }
        if (rpcService != null) {
            rpcService.stop();
        }
        System.out.println("Application stopped");
    }
}

