package com.example.bytebuddy.CompletableFuture.completable_future.part1.apply;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertEquals;

public class ThenApply extends Demo {

    @Test
    public void testThenApply() throws InterruptedException, ExecutionException {
        CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet("single"));

        CompletionStage<String> stage = stage1.thenApply(
                s -> s.toUpperCase());

        assertEquals("SINGLE", stage.toCompletableFuture().get());
    }
}
