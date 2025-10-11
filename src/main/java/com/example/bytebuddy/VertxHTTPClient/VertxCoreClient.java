package com.example.bytebuddy.VertxHTTPClient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.CompositeFuture;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class VertxCoreClient extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        // 1. Create the Core HTTP Client
        HttpClient client = vertx.createHttpClient();

        List<String> urls = Arrays.asList(
            "httpbin.org/delay/2", // 2-second delay
            "httpbin.org/delay/1", // 1-second delay
            "httpbin.org/delay/3", // 3-second delay
            "invalid-domain-to-fail.com" // Will fail
        );

        System.out.println("Starting all " + urls.size() + " requests concurrently using Vert.x Core HTTP Client...");
        long startTime = System.currentTimeMillis();

        // 2. Create a List of Futures for each request
        List<Future<String>> requestFutures = urls.stream()
            .map(url -> fetchUrlAsync(client, url))
            .collect(Collectors.toList());

        // 3. Combine all individual Futures into a single CompositeFuture
        // The CompositeFuture will complete when all internal Futures have completed (either success or failure)
        CompositeFuture.all(new ArrayList<>(requestFutures))
            .onComplete(ar -> {
                long totalTime = System.currentTimeMillis() - startTime;
                System.out.println("------------------------------------");
                System.out.printf("All requests processed. Total time: %d ms\n", totalTime);

                if (ar.succeeded()) {
                    List<String> collectedResults = ar.result().list();
                    
                    // The collectedResults will contain the results of each individual Future<String>
                    System.out.println("\n*** Collected Results (Single Value) ***");
                    collectedResults.forEach(System.out::println);
                    
                    startPromise.complete();
                } else {
                    // This block catches catastrophic errors (e.g., failure to deploy the verticle)
                    System.err.println("Catastrophic failure during processing: " + ar.cause().getMessage());
                    startPromise.fail(ar.cause());
                }
            });
    }

    /**
     * Executes a single HTTP GET request asynchronously and maps the result/failure to a single Future<String>.
     */
    private Future<String> fetchUrlAsync(HttpClient client, String host) {
        Promise<String> promise = Promise.promise();
        
        // Split host for request configuration
        String uriPath = host.contains("/") ? host.substring(host.indexOf('/')) : "/";
        String hostname = host.contains("/") ? host.substring(0, host.indexOf('/')) : host;

        // Note: Vert.x Core client uses separate onSuccess/onFailure handlers
        client.request(io.vertx.core.http.HttpMethod.GET, 80, hostname, uriPath)
            .compose(request -> {
                // Send the request and attach a response handler
                return request.send()
                    .compose(response -> {
                        // The body handler is used to read the full body as a Buffer
                        return response.body().map(buffer -> 
                            String.format("URL: %s, Status: %d, Content Size: %d bytes (SUCCESS)", 
                                hostname, 
                                response.statusCode(), 
                                buffer.length())
                        );
                    });
            })
            .onSuccess(promise::complete) // Complete the promise with the successful result string
            .onFailure(error -> {
                // Handle the network error or HTTP failure, and complete the promise with an error string
                String result = String.format("URL: %s, FAILED: %s", hostname, error.getMessage());
                promise.complete(result);
            });

        return promise.future();
    }

    // Boilerplate for running the Verticle
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new VertxCoreClient());
    }
}