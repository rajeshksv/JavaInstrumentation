package com.example.bytebuddy.CompletableFuture.completable_future.part6;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GetNow extends Demo {

    @Test
    public void getNow() {
        CompletableFuture<String> future = CompletableFuture.completedFuture("value");
        assertEquals("value", future.getNow("default"));
        assertTrue(future.isDone());
    }

    @Test
    public void getNowValueIfAbsent() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> sleepAndGet("value"));
        assertEquals("default", future.getNow("default"));
        assertFalse(future.isDone());
    }
}
