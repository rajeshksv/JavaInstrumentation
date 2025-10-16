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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Netty RPC Client for making outbound RPC calls
 */
public class RpcClient {
    private final String host;
    private final int port;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, CompletableFuture<RpcResponse>> pendingRequests;
    private EventLoopGroup group;
    private Channel channel;
    private boolean connected = false;
    
    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.objectMapper = new ObjectMapper();
        this.pendingRequests = new ConcurrentHashMap<>();
    }
    
    /**
     * Connect to the RPC server
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
                        
                        // Custom handler for processing RPC messages
                        pipeline.addLast(new RpcClientHandler());
                    }
                });
        
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            this.channel = channelFuture.channel();
            this.connected = true;
            connectFuture.complete(null);
            System.out.println("RPC Client connected to " + host + ":" + port);
        } catch (Exception e) {
            connectFuture.completeExceptionally(e);
            System.err.println("Failed to connect to RPC server: " + e.getMessage());
        }
        
        return connectFuture;
    }
    
    /**
     * Make an RPC call
     */
    public CompletableFuture<RpcResponse> call(String method, Object... params) {
        if (!connected) {
            CompletableFuture<RpcResponse> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new IllegalStateException("Client not connected"));
            return failedFuture;
        }
        
        String messageId = java.util.UUID.randomUUID().toString();
        RpcRequest request = new RpcRequest(messageId, method, params);
        
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        pendingRequests.put(messageId, responseFuture);
        
        try {
            String jsonRequest = objectMapper.writeValueAsString(request);
            ByteBuf buffer = Unpooled.copiedBuffer(jsonRequest.getBytes());
            channel.writeAndFlush(buffer);
            System.out.println("In the client....");
            
            System.out.println("Sent RPC request: " + method + " with ID: " + messageId);
        } catch (Exception e) {
            pendingRequests.remove(messageId);
            responseFuture.completeExceptionally(e);
        }
        
        return responseFuture;
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
        System.out.println("RPC Client disconnected");
    }
    
    /**
     * Custom handler for processing RPC responses
     */
    private class RpcClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buffer = (ByteBuf) msg;
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.readBytes(bytes);
            buffer.release();
            
            String jsonResponse = new String(bytes);
            System.out.println("Received RPC response: " + jsonResponse);
            
            RpcResponse response = objectMapper.readValue(jsonResponse, RpcResponse.class);
            CompletableFuture<RpcResponse> future = pendingRequests.remove(response.getMessageId());
            
            if (future != null) {
                future.complete(response);
            }
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.err.println("RPC Client error: " + cause.getMessage());
            cause.printStackTrace();
            ctx.close();
        }
        
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("RPC Client connection closed");
            connected = false;
            
            // Complete all pending requests with connection error
            pendingRequests.values().forEach(future -> 
                future.completeExceptionally(new RuntimeException("Connection closed")));
            pendingRequests.clear();
        }
    }
}
