package com.example.bytebuddy.CompletableFuture.completable_future.part2;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class WhenComplete extends Demo {

    @Test
    public void testWhenCompleteSuccess() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.completedFuture("value")
                .whenComplete((value, t) -> {
                    if (t == null) {
                        logger.info("success: {}", value);
                    } else {
                        logger.warn("failure: {}", t.getMessage());
                    }
                });

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertEquals("value", future.get());
    }

    @Test
    public void testWhenCompleteError() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("exception"));
        future = future.whenComplete((value, t) -> {
                    if (t == null) {
                        logger.info("success: {}", value);
                    } else {
                        logger.warn("failure: {}", t.getMessage());
                    }
                });

        assertTrue(future.isDone());
        assertTrue(future.isCompletedExceptionally());
    }
}
