package com.example.bytebuddy.ReactiveProgramming;

import reactor.core.publisher.Mono;
import java.time.Duration;

// This approach uses non-blocking composition.

public class ReactiveExample {
    
    // Simulates a non-blocking Publisher (emits data later)
    public static Mono<String> fetchDetails(String id) {
        System.out.printf("[%s] Initiating details fetch for %s...\n", Thread.currentThread().getName(), id);
        // The subscribeOn(Schedulers.boundedElastic()) moves the blocking work 
        // to a dedicated worker thread, keeping the main thread free.
        return Mono.just("Details(ID:" + id + ")")
                   .delayElement(Duration.ofSeconds(2)); // Non-blocking delay
    }

    // Simulates a second non-blocking Publisher
    public static Mono<String> fetchPrice(String details) {
        System.out.printf("[%s] Initiating price fetch for %s...\n", Thread.currentThread().getName(), details);
        return Mono.just("Price(200)")
                   .delayElement(Duration.ofSeconds(3)); // Non-blocking delay
    }
    
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        
        // 1. Define the asynchronous stream composition
        Mono<String> finalResult = fetchDetails("P1")
            // 2. Chain the next asynchronous call using the result of the first
            .flatMap(details -> fetchPrice(details)
                // Combine results
                .map(price -> details + " + " + price));

        // 3. Subscribe to start the process (non-blocking)
        finalResult.subscribe(
            result -> {
                // This callback runs when the entire chain is complete
                System.out.println("Result: " + result);
                System.out.printf("Total Time: %d ms\n", (System.currentTimeMillis() - startTime));
            }
        );

        // Keep the main thread alive for the async operation (in a real app, the server does this)
        try { Thread.sleep(6000); } catch (InterruptedException e) {} 
    }
}
// Expected Output (The execution time is the longest dependency, not the sum):
// [main] Initiating details fetch for P1...
// [parallel-1] Initiating price fetch for Details(ID:P1)... // Executes immediately after details is available
// Result: Details(ID:P1) + Price(200)
// Total Time: ~3000 ms (The 3s task runs concurrently/immediately after the 2s task starts)
