package com.example.bytebuddy.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Examples demonstrating proper exception logging with full stack traces
 */
public class ExceptionLoggingExamples {
    
    private static final Logger logger = LoggerFactory.getLogger(ExceptionLoggingExamples.class);
    
    /**
     * Example 1: Basic exception logging with stack trace
     * Using logger.error(message, exception) - RECOMMENDED
     */
    public static void basicExceptionLogging() {
        logger.info("=== Basic exception logging example ===");
        try {
            throw new NullPointerException("Object is null");
        } catch (NullPointerException e) {
            // Best practice: Pass exception as last parameter
            logger.error("NullPointerException occurred: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 2: Logging with different log levels
     */
    public static void differentLogLevels() {
        logger.info("=== Exception logging with different log levels ===");
        try {
            performOperation();
        } catch (Exception e) {
            // Error level - for serious issues
            logger.error("Error level - Critical exception: {}", e.getMessage(), e);
            
            // Warn level - for recoverable issues
            logger.warn("Warn level - Recoverable exception: {}", e.getMessage(), e);
            
            // Info level - generally not recommended for exceptions
            logger.info("Info level - Exception info: {}", e.getMessage(), e);
            
            // Debug level - for detailed debugging
            logger.debug("Debug level - Detailed exception: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 3: Logging exception with context information
     */
    public static void loggingWithContext() {
        logger.info("=== Exception logging with context ===");
        String userId = "user123";
        String operation = "updateProfile";
        
        try {
            updateUserProfile(userId);
        } catch (Exception e) {
            // Include context in log message
            logger.error("Failed to {} for user {}: {}", 
                    operation, userId, e.getMessage(), e);
        }
    }
    
    /**
     * Example 4: Logging chained exceptions
     */
    public static void loggingChainedExceptions() {
        logger.info("=== Logging chained exceptions ===");
        try {
            processData();
        } catch (BusinessException e) {
            // Logger automatically includes cause chain in stack trace
            logger.error("BusinessException with chain: {}", e.getMessage(), e);
            
            // Manually log each level in chain
            Throwable current = e;
            int level = 0;
            while (current != null) {
                logger.error("Chain level {}: {} - {}", 
                        level, current.getClass().getSimpleName(), current.getMessage());
                current = current.getCause();
                level++;
            }
        }
    }
    
    /**
     * Example 5: Converting stack trace to string
     */
    public static void stackTraceToString() {
        logger.info("=== Converting stack trace to string ===");
        try {
            throw new IllegalArgumentException("Invalid argument");
        } catch (IllegalArgumentException e) {
            // Method 1: Using StringWriter and PrintWriter
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            logger.error("Exception stack trace as string:\n{}", stackTrace);
            
            // Method 2: Using logger (preferred)
            logger.error("Exception with stack trace:", e);
        }
    }
    
    /**
     * Example 6: Logging exception without stack trace
     */
    public static void loggingWithoutStackTrace() {
        logger.info("=== Logging exception without stack trace ===");
        try {
            throw new ValidationException("email", "invalid-email", "Invalid email format");
        } catch (ValidationException e) {
            // Only log message, no stack trace
            logger.warn("Validation failed: {}", e.getMessage());
            
            // Or with exception but without stack trace
            logger.warn("Validation failed: {} - Exception: {}", 
                    e.getMessage(), e.getClass().getSimpleName());
        }
    }
    
    /**
     * Example 7: Logging multiple exceptions
     */
    public static void loggingMultipleExceptions() {
        logger.info("=== Logging multiple exceptions ===");
        try {
            performMultipleOperations();
        } catch (Exception e) {
            logger.error("Primary exception: {}", e.getMessage(), e);
            
            // Log suppressed exceptions
            Throwable[] suppressed = e.getSuppressed();
            for (int i = 0; i < suppressed.length; i++) {
                logger.error("Suppressed exception {}: {}", 
                        i, suppressed[i].getMessage(), suppressed[i]);
            }
        }
    }
    
    /**
     * Example 8: Conditional exception logging
     */
    public static void conditionalExceptionLogging() {
        logger.info("=== Conditional exception logging ===");
        try {
            riskyOperation();
        } catch (NullPointerException e) {
            // Log at error level
            logger.error("NullPointerException: {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            // Log at warn level (less severe)
            logger.warn("IllegalArgumentException: {}", e.getMessage(), e);
        } catch (Exception e) {
            // Log unknown exceptions at error level
            logger.error("Unexpected exception: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 9: Logging exception with additional metadata
     */
    public static void loggingWithMetadata() {
        logger.info("=== Logging exception with metadata ===");
        String requestId = "req-12345";
        String userId = "user-67890";
        long timestamp = System.currentTimeMillis();
        
        try {
            processRequest(requestId, userId);
        } catch (Exception e) {
            // Include metadata in log message
            logger.error("Exception in request {} for user {} at {}: {}", 
                    requestId, userId, timestamp, e.getMessage(), e);
        }
    }
    
    /**
     * Example 10: Logging exception and re-throwing
     */
    public static void logAndRethrow() {
        logger.info("=== Logging exception and re-throwing ===");
        try {
            try {
                performOperation();
            } catch (IllegalArgumentException e) {
                // Log the exception
                logger.error("Caught exception, will re-throw: {}", e.getMessage(), e);
                // Re-throw
                throw e;
            }
        } catch (IllegalArgumentException e) {
            logger.error("Caught re-thrown exception: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 11: Logging exception with custom message format
     */
    public static void customMessageFormat() {
        logger.info("=== Custom exception message format ===");
        try {
            throw new BusinessException("ERR_001", "Business rule violation");
        } catch (BusinessException e) {
            // Custom formatted message
            String customMessage = String.format(
                    "[ErrorCode: %s] [Message: %s] [Class: %s]",
                    e.getErrorCode(), e.getMessage(), e.getClass().getSimpleName());
            logger.error("{}", customMessage, e);
        }
    }
    
    /**
     * Example 12: Logging exception stack trace elements
     */
    public static void loggingStackTraceElements() {
        logger.info("=== Logging exception stack trace elements ===");
        try {
            method1();
        } catch (Exception e) {
            logger.error("Exception occurred: {}", e.getMessage(), e);
            
            // Log individual stack trace elements
            StackTraceElement[] stackTrace = e.getStackTrace();
            logger.info("Stack trace has {} elements", stackTrace.length);
            
            for (int i = 0; i < Math.min(10, stackTrace.length); i++) {
                StackTraceElement element = stackTrace[i];
                logger.info("  [{}] {}.{}({}:{})", 
                        i,
                        element.getClassName(),
                        element.getMethodName(),
                        element.getFileName(),
                        element.getLineNumber());
            }
        }
    }
    
    // Helper methods
    
    private static void performOperation() {
        throw new IllegalArgumentException("Operation failed");
    }
    
    private static void updateUserProfile(String userId) {
        throw new RuntimeException("Database connection failed");
    }
    
    private static void processData() throws BusinessException {
        try {
            validateData();
        } catch (ValidationException e) {
            throw new BusinessException("VAL_001", "Data processing failed", e);
        }
    }
    
    private static void validateData() throws ValidationException {
        throw new ValidationException("data", "invalid", "Data validation failed");
    }
    
    private static void performMultipleOperations() throws Exception {
        Exception primary = new Exception("Primary exception");
        primary.addSuppressed(new IllegalArgumentException("Suppressed 1"));
        primary.addSuppressed(new NullPointerException("Suppressed 2"));
        throw primary;
    }
    
    private static void riskyOperation() {
        throw new NullPointerException("Null pointer");
    }
    
    private static void processRequest(String requestId, String userId) {
        throw new RuntimeException("Request processing failed");
    }
    
    private static void method1() {
        method2();
    }
    
    private static void method2() {
        method3();
    }
    
    private static void method3() {
        throw new RuntimeException("Exception in method3");
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
     * Run all exception logging examples
     */
    public static void runAllExamples() {
        logger.info("\n" + repeat("=", 80));
        logger.info("EXCEPTION LOGGING EXAMPLES");
        logger.info(repeat("=", 80) + "\n");
        
        basicExceptionLogging();
        differentLogLevels();
        loggingWithContext();
        loggingChainedExceptions();
        stackTraceToString();
        loggingWithoutStackTrace();
        loggingMultipleExceptions();
        conditionalExceptionLogging();
        loggingWithMetadata();
        logAndRethrow();
        customMessageFormat();
        loggingStackTraceElements();
        
        logger.info("\n" + repeat("=", 80));
        logger.info("All exception logging examples completed");
        logger.info(repeat("=", 80) + "\n");
    }
    
    public static void main(String[] args) {
        runAllExamples();
    }
}
