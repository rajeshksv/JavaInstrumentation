package com.example.bytebuddy.JavaNIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioEchoServer {

    public static void main(String[] args) throws IOException {
        // 1. Open Selector (The I/O Multiplexer)
        Selector selector = Selector.open();

        // 2. Open ServerSocketChannel and Bind to Port
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress("localhost", 8080));
        
        // IMPORTANT: Set channel to non-blocking mode
        serverChannel.configureBlocking(false);

        // 3. Register the Server Channel with the Selector
        // We are interested in OP_ACCEPT events (new connection requests)
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("NIO Echo Server started on port 8080. Waiting for events...");

        // 4. The Event Loop (Reactor Pattern)
        while (true) {
            // Block until at least one registered channel is ready for an operation.
            selector.select(); 

            // Get a set of keys for the channels that are ready.
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove(); // Must remove the key after processing

                try {
                    if (key.isAcceptable()) {
                        handleAccept(selector, key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                    // OP_WRITE is usually handled internally after a read/process cycle
                } catch (IOException e) {
                    // Client disconnected or error occurred; close the channel
                    key.cancel();
                    key.channel().close();
                }
            }
        }
    }

    private static void handleAccept(Selector selector, SelectionKey key) throws IOException {
        // Cast channel from the key back to its specific type
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel(); 
        
        // Accept the new connection; this is non-blocking here because isAcceptable was true
        SocketChannel clientChannel = serverChannel.accept(); 
        
        // Configure the new client channel to be non-blocking
        clientChannel.configureBlocking(false);

        // Register the client channel with the Selector for reading
        clientChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(256));
        System.out.println("Client connected: " + clientChannel.getRemoteAddress());
    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment(); // Retrieve the attached buffer

        // Read data into the buffer. This call is non-blocking.
        int bytesRead = clientChannel.read(buffer); 

        if (bytesRead > 0) {
            // 1. Flip: switch buffer from write mode to read mode
            buffer.flip();
            
            // 2. Process: read bytes from the buffer (echo logic)
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);
            String message = new String(data).trim();
            System.out.println("Echoing back: " + message);

            // 3. Prepare for Write: Re-flip to prepare buffer for writing
            buffer.flip(); 
            
            // 4. Write: Echo the data back to the client
            while (buffer.hasRemaining()) {
                clientChannel.write(buffer);
            }

            // 5. Clear: prepare buffer for next read cycle
            buffer.clear();
        } else if (bytesRead == -1) {
            // Client closed connection gracefully
            key.cancel();
            clientChannel.close();
            System.out.println("Client disconnected.");
        }
    }
}
