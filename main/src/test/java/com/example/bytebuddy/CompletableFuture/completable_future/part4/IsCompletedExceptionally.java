package com.example.bytebuddy.CompletableFuture.completable_future.part4;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IsCompletedExceptionally extends Demo {

    @Test
    public void testIsCompletedExceptionallyFalse() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = CompletableFuture.completedFuture("value");

        assertTrue(future.isDone());
        assertFalse(future.isCompletedExceptionally());
        assertFalse(future.isCancelled());
        assertEquals("value", future.get());
    }

    @Test
    public void testIsCompletedExceptionallyTrue() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("exception"));

        assertTrue(future.isDone());
        assertTrue(future.isCompletedExceptionally());
        assertFalse(future.isCancelled());
    }
}
