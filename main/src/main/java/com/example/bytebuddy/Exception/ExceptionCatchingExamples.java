package com.example.bytebuddy.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Examples demonstrating various ways to catch exceptions
 */
public class ExceptionCatchingExamples {
    
    private static final Logger logger = LoggerFactory.getLogger(ExceptionCatchingExamples.class);
    
    /**
     * Example 1: Basic try-catch
     */
    public static void basicTryCatch() {
        logger.info("=== Basic try-catch example ===");
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            logger.error("Caught ArithmeticException: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 2: Multiple catch blocks
     * Order matters: more specific exceptions first
     */
    public static void multipleCatchBlocks() {
        logger.info("=== Multiple catch blocks example ===");
        try {
            String str = null;
            int length = str.length();
            int result = 10 / 0;
        } catch (NullPointerException e) {
            logger.error("Caught NullPointerException: {}", e.getMessage(), e);
        } catch (ArithmeticException e) {
            logger.error("Caught ArithmeticException: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Caught general Exception: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 3: Multi-catch (Java 7+)
     * Catching multiple exception types in one catch block
     */
    public static void multiCatch() {
        logger.info("=== Multi-catch example (Java 7+) ===");
        try {
            String str = null;
            int length = str.length();
        } catch (NullPointerException | IllegalArgumentException e) {
            logger.error("Caught NullPointerException or IllegalArgumentException: {}", 
                    e.getMessage(), e);
        }
    }
    
    /**
     * Example 4: Try-catch-finally
     * Finally block always executes
     */
    public static void tryCatchFinally() {
        logger.info("=== Try-catch-finally example ===");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("non_existent_file.txt");
        } catch (FileNotFoundException e) {
            logger.error("Caught FileNotFoundException: {}", e.getMessage(), e);
        } finally {
            logger.info("Finally block executed - cleaning up resources");
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("Error closing file: {}", e.getMessage(), e);
                }
            }
        }
    }
    
    /**
     * Example 5: Try-with-resources (Java 7+)
     * Automatically closes resources
     */
    public static void tryWithResources() {
        logger.info("=== Try-with-resources example ===");
        try (FileInputStream fis = new FileInputStream("non_existent_file.txt")) {
            // File operations
        } catch (FileNotFoundException e) {
            logger.error("Caught FileNotFoundException: {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error("Caught IOException: {}", e.getMessage(), e);
        }
        // fis is automatically closed here
    }
    
    /**
     * Example 6: Nested try-catch
     */
    public static void nestedTryCatch() {
        logger.info("=== Nested try-catch example ===");
        try {
            try {
                int[] array = {1, 2, 3};
                int value = array[10];
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.error("Inner catch - ArrayIndexOutOfBoundsException: {}", 
                        e.getMessage(), e);
                // Re-throw or handle
                throw new RuntimeException("Wrapped exception", e);
            }
        } catch (RuntimeException e) {
            logger.error("Outer catch - RuntimeException: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 7: Catching and re-throwing
     */
    public static void catchAndRethrow() {
        logger.info("=== Catch and re-throw example ===");
        try {
            performRiskyOperation();
        } catch (IllegalArgumentException e) {
            logger.error("Caught exception, re-throwing: {}", e.getMessage(), e);
            throw e; // Re-throw the same exception
        }
    }
    
    /**
     * Example 8: Catching and wrapping
     */
    public static void catchAndWrap() {
        logger.info("=== Catch and wrap example ===");
        try {
            performRiskyOperation();
        } catch (IllegalArgumentException e) {
            logger.error("Caught exception, wrapping in custom exception: {}", 
                    e.getMessage(), e);
            throw new CustomUncheckedException("Wrapped exception", e);
        }
    }
    
    /**
     * Example 9: Catching checked exception
     */
    public static void catchCheckedException() {
        logger.info("=== Catch checked exception example ===");
        try {
            throwCheckedException();
        } catch (CustomCheckedException e) {
            logger.error("Caught checked exception: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 10: Catching with specific exception details
     */
    public static void catchWithDetails() {
        logger.info("=== Catch with exception details example ===");
        try {
            List<String> list = new ArrayList<>();
            String item = list.get(5);
        } catch (IndexOutOfBoundsException e) {
            logger.error("Exception class: {}", e.getClass().getName());
            logger.error("Exception message: {}", e.getMessage());
            logger.error("Exception cause: {}", e.getCause());
            logger.error("Stack trace:", e);
            
            // Print stack trace elements
            StackTraceElement[] stackTrace = e.getStackTrace();
            logger.info("Stack trace elements count: {}", stackTrace.length);
            for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
                logger.info("  [{}] {}", i, stackTrace[i]);
            }
        }
    }
    
    /**
     * Example 11: Suppressed exceptions (try-with-resources)
     */
    public static void suppressedExceptions() {
        logger.info("=== Suppressed exceptions example ===");
        try (AutoCloseableResource resource = new AutoCloseableResource()) {
            resource.doSomething();
            throw new RuntimeException("Exception in try block");
        } catch (Exception e) {
            logger.error("Primary exception: {}", e.getMessage(), e);
            
            // Check for suppressed exceptions
            Throwable[] suppressed = e.getSuppressed();
            if (suppressed.length > 0) {
                logger.info("Suppressed exceptions count: {}", suppressed.length);
                for (Throwable t : suppressed) {
                    logger.error("Suppressed exception: {}", t.getMessage(), t);
                }
            }
        }
    }
    
    // Helper methods
    
    private static void performRiskyOperation() {
        throw new IllegalArgumentException("Invalid argument provided");
    }
    
    private static void throwCheckedException() throws CustomCheckedException {
        throw new CustomCheckedException("This is a checked exception");
    }
    
    /**
     * Helper class for demonstrating suppressed exceptions
     */
    static class AutoCloseableResource implements AutoCloseable {
        @Override
        public void close() throws Exception {
            logger.info("Closing resource");
            throw new IOException("Exception during resource close");
        }
        
        public void doSomething() {
            logger.info("Doing something with resource");
        }
    }
    
    /**
     * Helper method to repeat a string (Java 8 compatible)
     */
    private static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    /**
     * Run all exception catching examples
     */
    public static void runAllExamples() {
        logger.info("\n" + repeat("=", 80));
        logger.info("EXCEPTION CATCHING EXAMPLES");
        logger.info(repeat("=", 80) + "\n");
        
        basicTryCatch();
        multipleCatchBlocks();
        multiCatch();
        tryCatchFinally();
        tryWithResources();
        nestedTryCatch();
        catchAndRethrow();
        catchAndWrap();
        catchCheckedException();
        catchWithDetails();
        suppressedExceptions();
        
        logger.info("\n" + repeat("=", 80));
        logger.info("All exception catching examples completed");
        logger.info(repeat("=", 80) + "\n");
    }
    
    public static void main(String[] args) {
        runAllExamples();
    }
}
