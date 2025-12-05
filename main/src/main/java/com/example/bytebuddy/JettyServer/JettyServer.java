package com.example.bytebuddy.JettyServer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Jetty Web Server that makes outbound HTTP calls using Apache HTTP synchronous (non-NIO) client
 */
public class JettyServer {

    private Server server;
    private int port;

    public JettyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        // Create Jetty server instance
        server = new Server(port);

        // Create servlet context handler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Register servlet that makes outbound HTTP calls
        ServletHolder servletHolder = new ServletHolder(new OutboundHttpServlet());
        context.addServlet(servletHolder, "/*");

        // Start the server
        server.start();
        System.out.println("✅ Jetty Server started on port " + port);
        System.out.println("🌐 Server available at: http://localhost:" + port);
        System.out.println("📋 Available endpoints:");
        System.out.println("   GET  /health - Health check");
        System.out.println("   GET  /http-get?url=<target_url> - Make GET request to external URL");
        System.out.println("   POST /http-post?url=<target_url> - Make POST request to external URL");
        System.out.println("   GET  /example - Example GET request to httpbin.org");

        // Wait for server to stop
        server.join();
    }

    public void stop() throws Exception {
        if (server != null && server.isStarted()) {
            server.stop();
            System.out.println("🛑 Jetty Server stopped");
        }
    }

    public static void main(String[] args) {
        int port = 8081;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default port 8080");
            }
        }

        JettyServer jettyServer = new JettyServer(port);
        
        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                jettyServer.stop();
            } catch (Exception e) {
                System.err.println("Error stopping server: " + e.getMessage());
            }
        }));

        try {
            jettyServer.start();
        } catch (Exception e) {
            System.err.println("❌ Failed to start Jetty Server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

