package com.example.bytebuddy.CompletableFuture.completable_future.part5;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompleteAsync extends Demo {

    @Test
    public void testCompleteAsync() throws InterruptedException, ExecutionException {
        CompletableFuture<String> future1 = new CompletableFuture<>();

        assertFalse(future1.isDone());

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "value");
        sleep(1);

        assertTrue(future2.isDone());
        assertEquals("value", future2.get());

        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> {
            sleep(1);
            return "hello world";
        });

        sleep(2);
        assertTrue(supplyAsync.isDone());
        assertEquals("hello world", supplyAsync.get());
    }

}
