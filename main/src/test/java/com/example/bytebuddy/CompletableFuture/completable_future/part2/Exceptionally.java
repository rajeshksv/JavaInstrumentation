package com.example.bytebuddy.CompletableFuture.completable_future.part2;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class Exceptionally extends Demo {

    @Test
    public void testExceptionallySuccess() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.completedFuture("value")
                .exceptionally(t -> "failure: " + t.getMessage());

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertEquals("value", future.get());
    }

    @Test
    public void testExceptionallyError() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("exception"));
        future = future.exceptionally(t -> "failure: " + t.getMessage());

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertEquals("failure: exception", future.get());
    }
}
