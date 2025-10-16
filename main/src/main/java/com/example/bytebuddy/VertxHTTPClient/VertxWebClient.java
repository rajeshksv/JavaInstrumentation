package com.example.bytebuddy.VertxHTTPClient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.CompositeFuture;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.core.buffer.Buffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VertxWebClient extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        WebClient client = WebClient.create(vertx);

        List<String> urls = Arrays.asList(
        "http://httpbin.org/delay/2", // 2-second delay
            "http://httpbin.org/delay/1", // 1-second delay
            "http://httpbin.org/delay/3", // 3-second delay
            "http://invalid-domain-to-fail.com" // Will fail with a DNS error
        );

        System.out.println("Starting all " + urls.size() + " requests concurrently...");

        // 1. Create a List of Futures, one for each HTTP request
        List<Future<String>> requestFutures = urls.stream()
            .map(url -> fetchUrlAsync(client, url))
            .collect(Collectors.toList());

        // 2. Combine all individual Futures into a single CompositeFuture
        CompositeFuture.all(new ArrayList<>(requestFutures))
            .onSuccess(composite -> {
                // This block executes once ALL requests (success or failure) have finished.
                
                List<String> collectedResults = new ArrayList<>();
                
                // 3. Extract results from the CompositeFuture
                for (int i = 0; i < urls.size(); i++) {
                    String result = requestFutures.get(i).result();
                    collectedResults.add(result);
                }

                System.out.println("------------------------------------");
                System.out.println("*** Collected Results (Single Value) ***");
                collectedResults.forEach(System.out::println);
                System.out.println("------------------------------------");
                
                startPromise.complete();
            })
            .onFailure(error -> {
                // Note: CompositeFuture.all() fails fast only if an initial setup error occurs.
                // For HTTP errors (404/500), we handle them inside fetchUrlAsync.
                System.err.println("Unexpected failure in composite future: " + error.getMessage());
                startPromise.fail(error);
            });
    }

    /**
     * Executes a single HTTP GET request asynchronously and maps the result/failure to a single String.
     * * @param client The Vert.x WebClient instance.
     * @param url The URL to fetch.
     * @return A Future<String> that completes with a formatted result or error message.
     */
    private Future<String> fetchUrlAsync(WebClient client, String url) {
        // We use a Promise to manage the completion of this single request
        Promise<String> promise = Promise.promise();

        client.getAbs(url).send()
            .onSuccess(response -> {
                // 4. Success Path: Return status and size
                int status = response.statusCode();
                int size = response.body() != null ? response.body().length() : 0;
                
                String result = String.format("URL: %s, Status: %d, Content Size: %d bytes (SUCCESS)", 
                                              url, status, size);
                promise.complete(result);
            })
            .onFailure(error -> {
                // 5. Failure Path: Return a structured error message
                String result = String.format("URL: %s, FAILED: %s", url, error.getMessage());
                promise.complete(result);
            });

        return promise.future();
    }

    // Boilerplate for running the Verticle
    public static void main(String[] args) {
        io.vertx.core.Vertx.vertx().deployVerticle(new VertxWebClient());
    }
}
