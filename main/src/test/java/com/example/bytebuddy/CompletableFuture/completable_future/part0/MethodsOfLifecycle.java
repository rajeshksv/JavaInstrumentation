package com.example.bytebuddy.CompletableFuture.completable_future.part0;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MethodsOfLifecycle extends Demo {

    @Test
    public void test() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        CompletableFuture<String> future = new CompletableFuture<>(); // creating an incomplete future

        executorService.submit(() -> {
            TimeUnit.SECONDS.sleep(1);
            future.complete("value"); // completing the incomplete future
            return null;                    // If anything is returned, it can be captured by  `value = executorService.submit()`
        });

        while (!future.isDone()) { // checking the future for completion
            TimeUnit.SECONDS.sleep(2);
        }

        String result = future.get(); // reading value of the completed future
        logger.info("result: {}", result);

        executorService.shutdown();
    }
}
