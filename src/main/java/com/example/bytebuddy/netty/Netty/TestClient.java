package com.example.bytebuddy.netty.Netty;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

/**
 * Test client to demonstrate the Netty RPC server functionality
 */
public class TestClient {
    private final String host;
    private final int port;
    private final ObjectMapper objectMapper;
    private EventLoopGroup group;
    private Channel channel;
    private boolean connected = false;
    
    public TestClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Connect to the server
     */
    public CompletableFuture<Void> connect() {
        CompletableFuture<Void> connectFuture = new CompletableFuture<>();
        
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        
                        // Length field decoder for handling message boundaries
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4));
                        pipeline.addLast(new LengthFieldPrepender(4));
                        
                        // Custom handler for processing responses
                        pipeline.addLast(new TestClientHandler());
                    }
                });
        
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            this.channel = channelFuture.channel();
            this.connected = true;
            connectFuture.complete(null);
            System.out.println("Test client connected to " + host + ":" + port);
        } catch (Exception e) {
            connectFuture.completeExceptionally(e);
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
        
        return connectFuture;
    }
    
    /**
     * Send an RPC request
     */
    public void sendRequest(String method, Object... params) {
        if (!connected) {
            System.err.println("Client not connected");
            return;
        }
        
        String messageId = java.util.UUID.randomUUID().toString();
        RpcRequest request = new RpcRequest(messageId, method, params);
        
        try {
            String jsonRequest = objectMapper.writeValueAsString(request);
            System.out.println("Sent RPC request: " + jsonRequest);
            ByteBuf buffer = Unpooled.copiedBuffer(jsonRequest.getBytes());
            channel.writeAndFlush(buffer);
            
            System.out.println("Sent RPC request: " + method + " with ID: " + messageId);
        } catch (Exception e) {
            System.err.println("Error sending request: " + e.getMessage());
        }
    }
    
    /**
     * Close the client connection
     */
    public void close() {
        if (channel != null && channel.isActive()) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
        connected = false;
        System.out.println("Test client disconnected");
    }
    
    /**
     * Custom handler for processing responses
     */
    private class TestClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buffer = (ByteBuf) msg;
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.readBytes(bytes);
            buffer.release();
            
            String jsonResponse = new String(bytes);
            System.out.println("Received response: " + jsonResponse);
            
            RpcResponse response = objectMapper.readValue(jsonResponse, RpcResponse.class);
            if (response.isSuccess()) {
                System.out.println("✓ Success: " + response.getResult());
            } else {
                System.out.println("✗ Error: " + response.getError());
            }
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.err.println("Test client error: " + cause.getMessage());
            cause.printStackTrace();
            ctx.close();
        }
        
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Connection to server closed");
            connected = false;
        }
    }
    
    public static void main(String[] args) {
        TestClient client = new TestClient("localhost", 8080);
        
        client.connect().thenRun(() -> {
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("Test client ready. Enter commands:");
            System.out.println("  echo <message>     - Send echo request");
            System.out.println("  calculate <a> <b>  - Send calculation request");
            System.out.println("  quit               - Exit");
            
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                
                if ("quit".equalsIgnoreCase(input)) {
                    break;
                } else if (input.startsWith("echo ")) {
                    String message = input.substring(5);
                    client.sendRequest("echo", message);
                } else if (input.startsWith("calculate ")) {
                    String[] parts = input.split(" ");
                    if (parts.length >= 3) {
                        try {
                            double a = Double.parseDouble(parts[1]);
                            double b = Double.parseDouble(parts[2]);
                            client.sendRequest("calculate", a, b);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid numbers for calculation");
                        }
                    } else {
                        System.err.println("Usage: calculate <number1> <number2>");
                    }
                } else if (!input.isEmpty()) {
                    System.out.println("Unknown command. Try 'echo <message>' or 'calculate <a> <b>'");
                }
            }
            
            scanner.close();
            client.close();
        }).exceptionally(throwable -> {
            System.err.println("Failed to connect: " + throwable.getMessage());
            return null;
        });
    }
}

