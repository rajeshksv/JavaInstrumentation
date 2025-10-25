package com.example.bytebuddy.CompletableFuture.completable_future.part1.accept;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertNull;

public class ThenAccept extends Demo {

    @Test
    public void testThenAccept() throws InterruptedException, ExecutionException {
        CompletableFuture<String> stage1 = supplyAsync(() -> sleepAndGet("single"));

        CompletionStage<Void> stage = stage1.thenAccept(
                s -> logger.info("consumes the single: {}", s));

        assertNull(stage.toCompletableFuture().get());
    }
}
