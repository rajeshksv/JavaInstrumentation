package com.example.bytebuddy.CompletableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class Example {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        System.out.println("=== Method 1: Callbacks execute on pool thread (correct way) ===");
        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " Doing Operation");
            return "hello";
        }, executor)
        .thenApply(s -> fn1())
        .thenApply(s -> fn2())
        .join(); // This ensures callbacks execute on the pool thread
    }


    public static String fn1(){
        System.out.println(Thread.currentThread().getName() + " fn1");
        return "fn1";
    }
    
    public static String fn2(){
        System.out.println(Thread.currentThread().getName() + " fn2");
        return "fn2";
    }
}


class Operation implements Supplier {
    CompletableFuture<String> f1;

    public Operation(CompletableFuture<String> f1) {
        this.f1 = f1;
    }

    public String get(){
        System.out.println(Thread.currentThread().getName() + " Doing Operation");
        f1.complete("hello");
        return "hello";
    }
}

class Runnable1 implements Runnable {
    public void run(){
        System.out.println(Thread.currentThread().getName() + " Inside runnable1");
    }
}