package com.example.bytebuddy.netty.Netty;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.concurrent.CompletableFuture;

/**
 * Simple RPC Service that the main server will call
 */
public class SimpleRpcService {
    private final int port;
    private final ObjectMapper objectMapper;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private boolean started = false;
    
    public SimpleRpcService(int port) {
        this.port = port;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Start the RPC service
     */
    public CompletableFuture<Void> start() {
        CompletableFuture<Void> startFuture = new CompletableFuture<>();
        
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        
                        // Length field decoder for handling message boundaries
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4));
                        pipeline.addLast(new LengthFieldPrepender(4));
                        
                        // Custom handler for processing RPC messages
                        pipeline.addLast(new RpcServiceHandler());
                    }
                });
        
        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            this.serverChannel = channelFuture.channel();
            this.started = true;
            startFuture.complete(null);
            System.out.println("Simple RPC Service started on port " + port);
        } catch (Exception e) {
            startFuture.completeExceptionally(e);
            System.err.println("Failed to start RPC service: " + e.getMessage());
        }
        
        return startFuture;
    }
    
    /**
     * Stop the RPC service
     */
    public void stop() {
        if (serverChannel != null && serverChannel.isActive()) {
            serverChannel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        started = false;
        System.out.println("Simple RPC Service stopped");
    }
    
    /**
     * Custom handler for processing RPC requests
     */
    private class RpcServiceHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buffer = (ByteBuf) msg;
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.readBytes(bytes);
            buffer.release();
            
            String jsonRequest = new String(bytes);
            System.out.println("RPC Service received request: " + jsonRequest);
            
            RpcRequest request = objectMapper.readValue(jsonRequest, RpcRequest.class);
            
            // Process the request
            processRequest(ctx, request);
        }
        
        private void processRequest(ChannelHandlerContext ctx, RpcRequest request) {
            try {
                String method = request.getMethod();
                java.util.List<Object> params = request.getParams();
                //Object[] params = paramsList != null ? paramsList.toArray(new Object[0]) : new Object[0];
                
                Object result = null;
                String error = null;
                
                System.out.println("Method name " + method + " Params: " + params);
                
                // Simple business logic based on method name
                switch (method) {
                    case "processData":
                        if (params != null && params.size() >= 2) {
                            String originalMethod = (String) params.get(0);
                            java.util.List<Object> originalParams = (java.util.List<Object>) params.get(1);
                            result = "Processed: " + originalMethod + " with " + originalParams.size() + " parameters";
                        } else {
                            error = "Invalid parameters for processData";
                        }
                        break;
                    case "calculate":
                        System.out.println("Params: " + params);
                        if (params != null && ((java.util.List<Object>) params.get(0)).size() >= 2) {
                            try {        
                                double a = Double.parseDouble(((java.util.List<Object>) params.get(0)).get(0).toString());
                                double b = Double.parseDouble(((java.util.List<Object>) params.get(0)).get(1).toString());
                                result = a + b;
                            } catch (NumberFormatException e) {
                                error = "Invalid numbers for calculation";
                            }
                        } else {
                            error = "Invalid parameters for calculate";
                        }
                        break;
                    case "echo":
                        if (params != null && params.size() >= 1) {
                            result = "Echo: " + params.get(0);
                        } else {
                            error = "No message to echo";
                        }
                        break;
                    default:
                        error = "Unknown method: " + method;
                }
                
                // Send response
                RpcResponse response;
                if (error != null) {
                    response = new RpcResponse(request.getMessageId(), error);
                } else {
                    response = new RpcResponse(request.getMessageId(), result);
                }
                
                sendResponse(ctx, response);
            } catch (Exception e) {
                System.err.println("Error processing request: " + e.getMessage());
                sendErrorResponse(ctx, request.getMessageId(), e.getMessage());
            }
        }
        
        private void sendResponse(ChannelHandlerContext ctx, RpcResponse response) {
            try {
                String jsonResponse = objectMapper.writeValueAsString(response);
                ByteBuf buffer = Unpooled.copiedBuffer(jsonResponse.getBytes());
                ctx.writeAndFlush(buffer);
                System.out.println("RPC Service sent response: " + jsonResponse);
            } catch (Exception e) {
                System.err.println("Error sending response: " + e.getMessage());
            }
        }
        
        private void sendErrorResponse(ChannelHandlerContext ctx, String messageId, String error) {
            try {
                RpcResponse errorResponse = new RpcResponse(messageId, error);
                String jsonResponse = objectMapper.writeValueAsString(errorResponse);
                ByteBuf buffer = Unpooled.copiedBuffer(jsonResponse.getBytes());
                ctx.writeAndFlush(buffer);
                System.out.println("RPC Service sent error response: " + jsonResponse);
            } catch (Exception e) {
                System.err.println("Error sending error response: " + e.getMessage());
            }
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.err.println("RPC Service error: " + cause.getMessage());
            cause.printStackTrace();
            ctx.close();
        }
        
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Client connected to RPC Service: " + ctx.channel().remoteAddress());
        }
        
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Client disconnected from RPC Service: " + ctx.channel().remoteAddress());
        }
    }
}

