package com.example.bytebuddy.CompletableFuture.completable_future.part1.apply;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import io.netty.util.concurrent.SingleThreadEventExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertEquals;

public class ThenCompose extends Demo {

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void testThenCompose() throws InterruptedException, ExecutionException {
        CompletionStage<String> stage1 = supplyAsync(() -> sleepAndGet("sequential1"));

        CompletionStage<String> stage = stage1.thenCompose(
                s -> supplyAsync(() -> sleepAndGet((s + " " + "sequential2").toUpperCase())));

        assertEquals("SEQUENTIAL1 SEQUENTIAL2", stage.toCompletableFuture().get());

        CompletableFuture<String> supplyAsync = supplyAsync(() -> sleepAndGet("checking1"), executorService);
        CompletableFuture<String> thenCompose = supplyAsync.thenCompose(s -> testMethod(s));
        CompletableFuture<Void> thenAccept = thenCompose.thenAccept(s -> logger.info(Thread.currentThread().getName() + " After compose, i m running callback " + s));
        thenAccept.get();
        
    }

    public CompletableFuture<String> testMethod(String s) {
        logger.info(Thread.currentThread().getName() + " " + s);
        return supplyAsync(()-> sleepAndGet("checking2"));
    }
}
