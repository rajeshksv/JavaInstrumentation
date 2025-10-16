package com.example.bytebuddy.AsyncHTTPClient;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Example {

    public static void main(String[] args) throws Exception {
        // 1. Define the URLs to fetch concurrently
        List<String> urls = Arrays.asList(
            "http://httpbin.org/delay/2", // 2-second delay
            "http://httpbin.org/delay/1", // 1-second delay
            "http://httpbin.org/delay/3"  // 3-second delay
        );

        // 2. Initialize the Asynchronous Client
        // The client manages its own pool of I/O threads internally (based on NIO)
        CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
        client.start();

        // 3. Setup synchronization for the main thread
        final CountDownLatch latch = new CountDownLatch(urls.size());
        final AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
        final AtomicInteger successCount = new AtomicInteger(0);

        System.out.println("Starting all " + urls.size() + " requests concurrently...");
        System.out.println("------------------------------------");

        // 4. Iterate and execute each request asynchronously
        for (final String url : urls) {
            final HttpGet request = new HttpGet(url);

            client.execute(request, new FutureCallback<HttpResponse>() {
                
                @Override
                public void completed(final HttpResponse response) {
                    try {
                        String status = response.getStatusLine().toString();
                        String content = EntityUtils.toString(response.getEntity());
                        
                        successCount.incrementAndGet();
                        long duration = System.currentTimeMillis() - startTime.get();

                        System.out.printf("[%s] SUCCESS: Status=%s, Size=%d bytes, Duration=%d ms\n", Thread.currentThread().getName(), status, content.length(), duration);
                    } catch (IOException e) {
                        failed(e); // Propagate IO errors to the failed method
                    } finally {
                        latch.countDown(); // Decrement the latch regardless of success/fail
                    }
                }

                @Override
                public void failed(final Exception ex) {
                    System.err.printf("[%s] FAILED: %s for URL: %s\n",
                            Thread.currentThread().getName(), ex.getMessage(), url);
                    latch.countDown();
                }

                @Override
                public void cancelled() {
                    System.out.printf("[%s] CANCELLED for URL: %s\n", Thread.currentThread().getName(), url);
                    latch.countDown();
                }
            });
        }

        // 5. Block the main thread until all callbacks have completed (latch reaches zero)
        latch.await();

        // 6. Shutdown the client gracefully
        client.close();

        long totalTime = System.currentTimeMillis() - startTime.get();
        System.out.println("------------------------------------");
        System.out.printf("All requests completed. Total time: %d ms (Expected time was > 3000 ms)\n", totalTime);
        System.out.printf("Successful requests: %d\n", successCount.get());
    }
}