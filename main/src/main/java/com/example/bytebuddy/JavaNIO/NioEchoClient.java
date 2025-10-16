package com.example.bytebuddy.JavaNIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioEchoClient {

    public static void main(String[] args) throws IOException {
        // 1. Open SocketChannel
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress("localhost", 8080));

        if (clientChannel.isConnected()) {
            String message = "Hello NIO Server!";
            
            // 2. Prepare Buffer for writing
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());

            // 3. Write data to the channel
            clientChannel.write(buffer);
            System.out.println("Sent: " + message);

            // 4. Prepare Buffer for reading response
            ByteBuffer readBuffer = ByteBuffer.allocate(256);
            int bytesRead = clientChannel.read(readBuffer); // Blocks until some data is available

            if (bytesRead > 0) {
                readBuffer.flip();
                byte[] responseBytes = new byte[readBuffer.remaining()];
                readBuffer.get(responseBytes);
                String response = new String(responseBytes).trim();
                System.out.println("Received: " + response);
            }
        }
        
        // 5. Close Channel
        clientChannel.close();
    }
}
