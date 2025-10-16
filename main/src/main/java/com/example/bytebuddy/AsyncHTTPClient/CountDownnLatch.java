package com.example.bytebuddy.AsyncHTTPClient;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CountDownnLatch {

    public static List<String> fetchAllUrls(List<String> urls) 
            throws InterruptedException, IOException, ExecutionException {
        
        // 1. Initialize the Asynchronous Client
        CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
        client.start();

        // 2. Thread-Safe List to collect all successful responses
        final List<String> allResponses = Collections.synchronizedList(new ArrayList<>());
        
        // 3. Setup synchronization for the main thread
        final CountDownLatch latch = new CountDownLatch(urls.size());
        final AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
        final AtomicInteger successCount = new AtomicInteger(0);


        System.out.println("Starting all " + urls.size() + " requests concurrently...");
        
        // 4. Execute each request asynchronously
        for (final String url : urls) {
            final HttpGet request = new HttpGet(url);

            client.execute(request, new FutureCallback<HttpResponse>() {
                
                @Override
                public void completed(final HttpResponse response) {
                    try {
                        successCount.incrementAndGet();

                        String status = response.getStatusLine().toString();
                        String content = EntityUtils.toString(response.getEntity());
                        long duration = System.currentTimeMillis() - startTime.get();

                        System.out.printf("[%s] SUCCESS: Status=%s, Size=%d bytes, Duration=%d ms\n", Thread.currentThread().getName(), status, response.getEntity().getContentLength(), duration);
                        allResponses.add("URL: " + url + ", Status: " + response.getStatusLine().getStatusCode() + ", Content Size: " + response.getEntity().getContentLength() + ", Response: " + response.getEntity().getContent());
                        
                    } catch (IOException e) {
                        // Handle the error by calling failed()
                        failed(e);
                    } finally {
                        latch.countDown();
                    }
                }

                @Override
                public void failed(final Exception ex) {
                    allResponses.add("URL: " + url + ", FAILED: " + ex.getMessage());
                    latch.countDown();
                }

                @Override
                public void cancelled() {
                    allResponses.add("URL: " + url + ", CANCELLED.");
                    latch.countDown();
                }
            });
        }

        // 5. Block the thread until all callbacks have completed (latch reaches zero)
        latch.await();

        // 6. Shutdown the client gracefully
        client.close();

        long totalTime = System.currentTimeMillis() - startTime.get();
        System.out.println("------------------------------------");
        System.out.printf("All requests processed. Total time: %d ms\n", totalTime);
        System.out.printf("Successful requests: %d\n", successCount.get());


        return allResponses;
    }

    public static void main(String[] args) {
        List<String> urls = Arrays.asList(
       "http://httpbin.org/delay/2", 
            "http://httpbin.org/delay/1", 
            "http://httpbin.org/delay/3",
            "http://invalid-domain-to-fail.com" // Example of a failed request
        );

        try {
            List<String> results = fetchAllUrls(urls);
            
            System.out.println("\n*** Collected Results (Single Value) ***");
            results.forEach(System.out::println);
            
        } catch (InterruptedException | IOException | ExecutionException e) {
            System.err.println("An unexpected error occurred during execution: " + e.getMessage());
        }
    }
}
