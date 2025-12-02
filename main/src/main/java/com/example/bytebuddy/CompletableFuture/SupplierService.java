package com.example.bytebuddy.CompletableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SupplierService {
    // Simulate fetching price from Supplier 1
    public CompletableFuture<Double> fetchPriceFromSupplier1(String productId) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate some delay in fetching the price
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Simulate a random price for Supplier 1
            return 50.0 + Math.random() * 50;
        });
    }

    // Simulate fetching price from Supplier 2
    public CompletableFuture<Double> fetchPriceFromSupplier2(String productId) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate some delay in fetching the price
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Simulate a random price for Supplier 2
            return 40.0 + Math.random() * 60;
        });
    }

    // Simulate fetching price from Supplier 3
    public CompletableFuture<Double> fetchPriceFromSupplier3(String productId) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate some delay in fetching the price
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Simulate a random price for Supplier 3
            return 45.0 + Math.random() * 55;
        });
    }

    public static void main(String[] args) {
        SupplierService supplierService = new SupplierService();

        CompletableFuture<Double> priceFromSupplier1 = supplierService.fetchPriceFromSupplier1("product123");
        CompletableFuture<Double> priceFromSupplier2 = supplierService.fetchPriceFromSupplier2("product123");
        CompletableFuture<Double> priceFromSupplier3 = supplierService.fetchPriceFromSupplier3("product123");

        CompletableFuture<Object> firstCompletedFuture = CompletableFuture.anyOf(
                priceFromSupplier1, priceFromSupplier2, priceFromSupplier3);

        try {
            // Wait for the first supplier to provide the price
            Object result = firstCompletedFuture.get();

            if (result instanceof Double) {
                double price = (Double) result;
                System.out.println("The first available price is: $" + price);
            } else {
                System.out.println("No price available from suppliers at the moment.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
