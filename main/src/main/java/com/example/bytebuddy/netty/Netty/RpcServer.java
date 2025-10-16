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
 * Netty RPC Server that handles incoming requests and makes outbound RPC calls
 */
public class RpcServer {
    private final int port;
    private final ObjectMapper objectMapper;
    private final RpcClient rpcClient;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private boolean started = false;
    
    public RpcServer(int port, String rpcServerHost, int rpcServerPort) {
        this.port = port;
        this.objectMapper = new ObjectMapper();
        this.rpcClient = new RpcClient(rpcServerHost, rpcServerPort);
    }
    
    /**
     * Start the RPC server
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
                        pipeline.addLast(new RpcServerHandler());
                    }
                });
        
        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            this.serverChannel = channelFuture.channel();
            this.started = true;
            startFuture.complete(null);
            System.out.println("RPC Server started on port " + port);
        } catch (Exception e) {
            startFuture.completeExceptionally(e);
            System.err.println("Failed to start RPC server: " + e.getMessage());
        }
        
        return startFuture;
    }
    
    /**
     * Connect to the external RPC server
     */
    public CompletableFuture<Void> connectToRpcServer() {
        return rpcClient.connect();
    }
    
    /**
     * Stop the RPC server
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
        rpcClient.close();
        started = false;
        System.out.println("RPC Server stopped");
    }
    
    /**
     * Custom handler for processing RPC requests and making outbound calls
     */
    private class RpcServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buffer = (ByteBuf) msg;
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.readBytes(bytes);
            buffer.release();
            
            String jsonRequest = new String(bytes);
            System.out.println("Received RPC request: " + jsonRequest);
            
            RpcRequest request = objectMapper.readValue(jsonRequest, RpcRequest.class);
            
            // Process the request and make outbound RPC call
            processRequest(ctx, request);
        }
        
        private void processRequest(ChannelHandlerContext ctx, RpcRequest request) {
            try {
                // Make outbound RPC call to external service
                rpcClient.call(request.getMethod(), request.getParams())
                    .thenAccept(response -> {
                        try {
                            // Create response for the client
                            RpcResponse serverResponse;
                            if (response.isSuccess()) {
                                serverResponse = new RpcResponse(request.getMessageId(), response.getResult());
                            } else {
                                serverResponse = new RpcResponse(request.getMessageId(), response.getError());
                            }
                            
                            // Send response back to client
                            sendResponse(ctx, serverResponse);
                        } catch (Exception e) {
                            System.err.println("Error processing RPC response: " + e.getMessage());
                            sendErrorResponse(ctx, request.getMessageId(), e.getMessage());
                        }
                    })
                    .exceptionally(throwable -> {
                        System.err.println("Outbound RPC call failed: " + throwable.getMessage());
                        sendErrorResponse(ctx, request.getMessageId(), "Outbound RPC call failed: " + throwable.getMessage());
                        return null;
                    });
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
                System.out.println("Sent RPC response: " + jsonResponse);
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
                System.out.println("Sent error response: " + jsonResponse);
            } catch (Exception e) {
                System.err.println("Error sending error response: " + e.getMessage());
            }
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.err.println("RPC Server error: " + cause.getMessage());
            cause.printStackTrace();
            ctx.close();
        }
        
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("New client connected: " + ctx.channel().remoteAddress());
        }
        
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Client disconnected: " + ctx.channel().remoteAddress());
        }
    }
}

