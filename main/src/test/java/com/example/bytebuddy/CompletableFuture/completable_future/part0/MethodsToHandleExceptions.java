package com.example.bytebuddy.CompletableFuture.completable_future.part0;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import java.util.concurrent.CompletableFuture;

public class MethodsToHandleExceptions extends Demo {

    @Test
    public void test() {
        CompletableFuture.supplyAsync(() -> 0)
                .thenApply(i -> { logger.info("stage 1: {}", i); return 1 / i; }) // executed and failed
                .thenApply(i -> { logger.info("stage 2: {}", i); return 1 / i; }) // skipped
                .whenComplete((value, t) -> {  // Identifies that the previous stage completed exceptionally, but does not recover from the exception
                    if (t == null) {
                        logger.info("success: {}", value);
                    } else {
                        logger.warn("failure: {}", t.getMessage()); // executed
                    }
                })
                .thenApply(i -> { logger.info("stage 3: {}", i); return 1 / i; }) // skipped
                .handle((value, t) -> { // Identifies that the previous stage completed exceptionally and replaces the exception with a default value
                    if (t == null) {
                        return value + 1;
                    } else {
                        return -1; // executed and recovered
                    }
                })
                .thenApply(i -> { logger.info("stage 4: {}", i); return 1 / i; }) // executed
                .join();
    }
}
