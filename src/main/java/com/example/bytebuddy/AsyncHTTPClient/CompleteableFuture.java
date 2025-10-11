package com.example.bytebuddy.AsyncHTTPClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CompleteableFuture {

    public static List<String> fetchAllUrls(List<String> urls) 
            throws InterruptedException, ExecutionException {

        // 1. Initialize the Asynchronous Client (Apache Async Client)
        // This client manages the threads for non-blocking I/O internally (NIO model).
        CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
        client.start();

        // 2. Map each URL to a CompletableFuture task
        List<CompletableFuture<String>> futures = urls.stream()
            .map(url -> executeAsyncGet(client, url))
            .collect(Collectors.toList());

        System.out.println("Starting all " + urls.size() + " requests concurrently using CompletableFuture...");

        // 3. Combine all CompletableFutures using CompletableFuture.allOf()
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        // 4. Transform the 'allOfFuture' (which completes with Void) into a future that holds the results (List<String>)
        CompletableFuture<List<String>> allResultsFuture = allOfFuture.thenApply(v -> 
            futures.stream()
                // Safely get the result from each completed future
                .map(CompletableFuture::join) 
                .collect(Collectors.toList())
        );

        // 5. Block and retrieve the single collected result value
        List<String> results = allResultsFuture.get();

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return results;
    }

    /**
     * Executes a single HTTP GET request asynchronously and wraps the result in a CompletableFuture.
     */
    private static CompletableFuture<String> executeAsyncGet(CloseableHttpAsyncClient client, String url) {
        // Create a CompletableFuture that will be completed by the callback
        final CompletableFuture<String> promise = new CompletableFuture<>();
        final HttpGet request = new HttpGet(url);

        client.execute(request, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(final HttpResponse response) {
                try {
                    String content = EntityUtils.toString(response.getEntity());
                    int status = response.getStatusLine().getStatusCode();
                    
                    String result = String.format("URL: %s, Status: %d, Content Size: %d bytes (SUCCESS)", 
                                                  url, status, content.length());
                    
                    promise.complete(result);
                } catch (IOException e) {
                    failed(e);
                }
            }

            @Override
            public void failed(final Exception ex) {
                String result = String.format("URL: %s, FAILED: %s", url, ex.getMessage());
                promise.complete(result);
            }

            @Override
            public void cancelled() {
                String result = String.format("URL: %s, CANCELLED.", url);
                promise.complete(result);
            }
        });

        return promise;
    }

    public static void main(String[] args) {
        List<String> urls = Arrays.asList(
            "http://httpbin.org/delay/2", 
            "http://httpbin.org/delay/1", 
            "http://httpbin.org/delay/3",
            "http://invalid-domain-to-fail.com"
        );

        long startTime = System.currentTimeMillis();

        try {
            List<String> results = fetchAllUrls(urls);
            long totalTime = System.currentTimeMillis() - startTime;
            
            System.out.println("------------------------------------");
            System.out.printf("All requests processed. Total time: %d ms\n", totalTime);
            System.out.println("\n*** Collected Results (List<String>) ***");
            results.forEach(System.out::println);
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("An unexpected error occurred during execution: " + e.getMessage());
        }
    }
}
