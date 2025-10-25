package com.example.bytebuddy.CompletableFuture.completable_future.part8;

import org.junit.Test;

import com.example.bytebuddy.CompletableFuture.completable_future.common.Demo;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;

public class GetNumberOfDependents extends Demo {

    @Test
    public void testGetNumberOfDependents() {
        CompletableFuture<String> future = CompletableFuture.completedFuture("value");
        assertEquals(0, future.getNumberOfDependents());
    }
}