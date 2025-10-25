package com.example.bytebuddy.CompletableFuture.completable_future.part2;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class Handle extends Demo {

    @Test
    public void testHandleSuccess() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.completedFuture("value")
                .handle((value, t) -> {
                    if (t == null) {
                        return value.toUpperCase();
                    } else {
                        return t.getMessage();
                    }
                });

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertEquals("VALUE", future.get());
    }

    @Test
    public void testHandleError() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("exception"));
        future = future.handle((value, t) -> {
                    if (t == null) {
                        return value.toUpperCase();
                    } else {
                        return "failure: " + t.getMessage();
                    }
                });

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertEquals("failure: exception", future.get());
    }
}
