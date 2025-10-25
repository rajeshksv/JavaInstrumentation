package com.example.bytebuddy.CompletableFuture.completable_future.part3;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertTrue;

public class FailedFuture extends Demo {

    @Test
    public void testCompletedFuture() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("exception"));

        assertTrue(future.isDone());
        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void testCompletedStage() {
        CompletionStage<String> future = new CompletableFuture<>();
        ((CompletableFuture<String>) future).completeExceptionally(new RuntimeException("exception"));

        assertTrue(future.toCompletableFuture().isDone());
        assertTrue(future.toCompletableFuture().isCompletedExceptionally());
    }
}

