package com.example.bytebuddy.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Comprehensive demonstration of all exception concepts:
 * - How exceptions happen
 * - How to catch exceptions
 * - Custom exceptions
 * - Exception chaining
 * - Proper logging with stack traces
 */
public class ComprehensiveExceptionDemo {
    
    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveExceptionDemo.class);
    
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
    
    public static void main(String[] args) {
        logger.info("\n" + repeat("=", 100));
        logger.info("COMPREHENSIVE EXCEPTION DEMONSTRATION");
        logger.info(repeat("=", 100) + "\n");
        
        // 1. Demonstrate how exceptions happen
        demonstrateExceptionOccurrence();
        
        // 2. Demonstrate exception catching
        demonstrateExceptionCatching();
        
        // 3. Demonstrate custom exceptions
        demonstrateCustomExceptions();
        
        // 4. Demonstrate exception chaining
        demonstrateExceptionChaining();
        
        // 5. Demonstrate proper logging
        demonstrateExceptionLogging();
        
        // 6. Real-world scenario combining all concepts
        demonstrateRealWorldScenario();
        
        logger.info("\n" + repeat("=", 100));
        logger.info("ALL DEMONSTRATIONS COMPLETED");
        logger.info(repeat("=", 100) + "\n");
    }
    
    /**
     * Section 1: How exceptions happen
     */
    private static void demonstrateExceptionOccurrence() {
        logger.info("\n" + repeat("-", 100));
        logger.info("SECTION 1: HOW EXCEPTIONS HAPPEN");
        logger.info(repeat("-", 100) + "\n");
        
        // NullPointerException
        logger.info("1.1 NullPointerException Example:");
        try {
            String str = null;
            int length = str.length();
        } catch (NullPointerException e) {
            logger.error("   NullPointerException caught: {}", e.getMessage(), e);
        }
        
        // ArrayIndexOutOfBoundsException
        logger.info("\n1.2 ArrayIndexOutOfBoundsException Example:");
        try {
            int[] array = {1, 2, 3};
            int value = array[10];
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("   ArrayIndexOutOfBoundsException caught: {}", e.getMessage(), e);
        }
        
        // NumberFormatException
        logger.info("\n1.3 NumberFormatException Example:");
        try {
            int number = Integer.parseInt("abc123");
        } catch (NumberFormatException e) {
            logger.error("   NumberFormatException caught: {}", e.getMessage(), e);
        }
        
        // ArithmeticException
        logger.info("\n1.4 ArithmeticException Example:");
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            logger.error("   ArithmeticException caught: {}", e.getMessage(), e);
        }
        
        // FileNotFoundException (Checked Exception)
        logger.info("\n1.5 FileNotFoundException (Checked Exception) Example:");
        try {
            FileInputStream fis = new FileInputStream("non_existent.txt");
            fis.close();
        } catch (FileNotFoundException e) {
            logger.error("   FileNotFoundException caught: {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error("   IOException caught: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Section 2: How to catch exceptions
     */
    private static void demonstrateExceptionCatching() {
        logger.info("\n" + repeat("-", 100));
        logger.info("SECTION 2: HOW TO CATCH EXCEPTIONS");
        logger.info(repeat("-", 100) + "\n");
        
        // Basic try-catch
        logger.info("2.1 Basic try-catch:");
        try {
            throw new IllegalArgumentException("Invalid argument");
        } catch (IllegalArgumentException e) {
            logger.error("   Caught: {}", e.getMessage(), e);
        }
        
        // Multiple catch blocks
        logger.info("\n2.2 Multiple catch blocks:");
        try {
            String str = null;
            int length = str.length();
        } catch (NullPointerException e) {
            logger.error("   Caught NullPointerException: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("   Caught general Exception: {}", e.getMessage(), e);
        }
        
        // Try-catch-finally
        logger.info("\n2.3 Try-catch-finally:");
        try {
            performOperation();
        } catch (Exception e) {
            logger.error("   Caught exception: {}", e.getMessage(), e);
        } finally {
            logger.info("   Finally block executed - cleanup code here");
        }
        
        // Try-with-resources
        logger.info("\n2.4 Try-with-resources:");
        try (FileInputStream fis = new FileInputStream("non_existent.txt")) {
            // File operations
        } catch (FileNotFoundException e) {
            logger.error("   FileNotFoundException in try-with-resources: {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error("   IOException in try-with-resources: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Section 3: Custom exceptions
     */
    private static void demonstrateCustomExceptions() {
        logger.info("\n" + repeat("-", 100));
        logger.info("SECTION 3: CUSTOM EXCEPTIONS");
        logger.info(repeat("-", 100) + "\n");
        
        // Custom unchecked exception
        logger.info("3.1 Custom Unchecked Exception:");
        try {
            throw new CustomUncheckedException("This is a custom unchecked exception");
        } catch (CustomUncheckedException e) {
            logger.error("   Caught CustomUncheckedException: {}", e.getMessage(), e);
        }
        
        // Custom checked exception
        logger.info("\n3.2 Custom Checked Exception:");
        try {
            throwCheckedException();
        } catch (CustomCheckedException e) {
            logger.error("   Caught CustomCheckedException: {}", e.getMessage(), e);
        }
        
        // Domain-specific custom exception (ValidationException)
        logger.info("\n3.3 Domain-Specific Custom Exception (ValidationException):");
        try {
            validateEmail("invalid-email");
        } catch (ValidationException e) {
            logger.error("   ValidationException - Field: {}, Value: {}, Message: {}", 
                    e.getFieldName(), e.getInvalidValue(), e.getMessage(), e);
        }
        
        // Business exception
        logger.info("\n3.4 Business Exception:");
        try {
            processOrder(-1);
        } catch (BusinessException e) {
            logger.error("   BusinessException - ErrorCode: {}, Message: {}", 
                    e.getErrorCode(), e.getMessage(), e);
        }
    }
    
    /**
     * Section 4: Exception chaining
     */
    private static void demonstrateExceptionChaining() {
        logger.info("\n" + repeat("-", 100));
        logger.info("SECTION 4: EXCEPTION CHAINING");
        logger.info(repeat("-", 100) + "\n");
        
        // Basic chaining
        logger.info("4.1 Basic Exception Chaining:");
        try {
            performDatabaseOperation();
        } catch (BusinessException e) {
            logger.error("   BusinessException: {}", e.getMessage(), e);
            logger.error("   Caused by: {}", 
                    e.getCause() != null ? e.getCause().getClass().getSimpleName() : "None", e);
        }
        
        // Multiple level chaining
        logger.info("\n4.2 Multiple Level Exception Chaining:");
        try {
            processUserRequest();
        } catch (BusinessException e) {
            logger.error("   Top level: {}", e.getMessage(), e);
            
            // Traverse chain
            Throwable current = e;
            int level = 0;
            while (current != null && level < 5) {
                logger.error("   Level {}: {} - {}", 
                        level, current.getClass().getSimpleName(), current.getMessage());
                current = current.getCause();
                level++;
            }
        }
        
        // Chaining with custom exceptions
        logger.info("\n4.3 Chaining Custom Exceptions:");
        try {
            validateAndSave();
        } catch (BusinessException e) {
            logger.error("   BusinessException with ValidationException chain: {}", 
                    e.getMessage(), e);
        }
    }
    
    /**
     * Section 5: Proper exception logging
     */
    private static void demonstrateExceptionLogging() {
        logger.info("\n" + repeat("-", 100));
        logger.info("SECTION 5: PROPER EXCEPTION LOGGING");
        logger.info(repeat("-", 100) + "\n");
        
        // Basic logging with stack trace
        logger.info("5.1 Basic Logging with Stack Trace:");
        try {
            throw new RuntimeException("Test exception");
        } catch (RuntimeException e) {
            logger.error("   Exception occurred: {}", e.getMessage(), e);
        }
        
        // Logging with context
        logger.info("\n5.2 Logging with Context:");
        String userId = "user-123";
        String operation = "updateProfile";
        try {
            updateProfile(userId);
        } catch (Exception e) {
            logger.error("   Failed to {} for user {}: {}", 
                    operation, userId, e.getMessage(), e);
        }
        
        // Logging chained exceptions
        logger.info("\n5.3 Logging Chained Exceptions:");
        try {
            complexOperation();
        } catch (BusinessException e) {
            logger.error("   Chained exception logged with full stack trace: {}", 
                    e.getMessage(), e);
        }
        
        // Logging stack trace elements
        logger.info("\n5.4 Logging Stack Trace Elements:");
        try {
            methodA();
        } catch (Exception e) {
            logger.error("   Exception: {}", e.getMessage(), e);
            StackTraceElement[] stackTrace = e.getStackTrace();
            logger.info("   Stack trace has {} elements", stackTrace.length);
            for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
                logger.info("     [{}] {}.{}({}:{})", 
                        i,
                        stackTrace[i].getClassName(),
                        stackTrace[i].getMethodName(),
                        stackTrace[i].getFileName(),
                        stackTrace[i].getLineNumber());
            }
        }
    }
    
    /**
     * Section 6: Real-world scenario combining all concepts
     */
    private static void demonstrateRealWorldScenario() {
        logger.info("\n" + repeat("-", 100));
        logger.info("SECTION 6: REAL-WORLD SCENARIO - USER REGISTRATION");
        logger.info(repeat("-", 100) + "\n");
        
        // Simulate user registration with various exception scenarios
        String email = "invalid-email";
        String userId = "user-456";
        
        try {
            logger.info("Attempting to register user: {}", userId);
            registerUser(userId, email);
            logger.info("User registration successful");
        } catch (ValidationException e) {
            logger.error("Registration failed - Validation error for field '{}' with value '{}': {}", 
                    e.getFieldName(), e.getInvalidValue(), e.getMessage(), e);
        } catch (BusinessException e) {
            logger.error("Registration failed - Business error [{}]: {}", 
                    e.getErrorCode(), e.getMessage(), e);
            if (e.getCause() != null) {
                logger.error("Root cause: {}", e.getCause().getClass().getSimpleName());
            }
        } catch (Exception e) {
            logger.error("Registration failed - Unexpected error: {}", e.getMessage(), e);
        } finally {
            logger.info("Registration process completed");
        }
    }
    
    // Helper methods for demonstrations
    
    private static void performOperation() {
        throw new RuntimeException("Operation failed");
    }
    
    private static void throwCheckedException() throws CustomCheckedException {
        throw new CustomCheckedException("This is a checked exception");
    }
    
    private static void validateEmail(String email) throws ValidationException {
        if (email == null || !email.contains("@")) {
            throw new ValidationException("email", email, "Invalid email format");
        }
    }
    
    private static void processOrder(int orderId) throws BusinessException {
        if (orderId < 0) {
            throw new BusinessException("ORD_001", "Invalid order ID: " + orderId);
        }
    }
    
    private static void performDatabaseOperation() throws BusinessException {
        try {
            // Simulate SQLException
            throw new java.sql.SQLException("Connection timeout", "08S01", 0);
        } catch (java.sql.SQLException e) {
            throw new BusinessException("DB_001", "Database operation failed", e);
        }
    }
    
    private static void processUserRequest() throws BusinessException {
        try {
            validateRequest();
        } catch (ValidationException e) {
            throw new BusinessException("REQ_001", "Request processing failed", e);
        }
    }
    
    private static void validateRequest() throws ValidationException {
        try {
            parseRequest();
        } catch (NumberFormatException e) {
            throw new ValidationException("requestId", "invalid", 
                    "Invalid request ID format", e);
        }
    }
    
    private static void parseRequest() {
        Integer.parseInt("invalid");
    }
    
    private static void validateAndSave() throws BusinessException {
        try {
            validateData();
        } catch (ValidationException e) {
            throw new BusinessException("SAVE_001", "Save operation failed", e);
        }
    }
    
    private static void validateData() throws ValidationException {
        throw new ValidationException("data", "invalid", "Data validation failed");
    }
    
    private static void updateProfile(String userId) {
        throw new RuntimeException("Database connection failed");
    }
    
    private static void complexOperation() throws BusinessException {
        try {
            innerOperation();
        } catch (IllegalArgumentException e) {
            throw new BusinessException("OP_001", "Complex operation failed", e);
        }
    }
    
    private static void innerOperation() {
        throw new IllegalArgumentException("Invalid argument");
    }
    
    private static void methodA() {
        methodB();
    }
    
    private static void methodB() {
        methodC();
    }
    
    private static void methodC() {
        throw new RuntimeException("Exception in methodC");
    }
    
    private static void registerUser(String userId, String email) 
            throws ValidationException, BusinessException {
        // Step 1: Validate email
        if (email == null || !email.contains("@")) {
            throw new ValidationException("email", email, "Invalid email format");
        }
        
        // Step 2: Validate user ID
        try {
            Integer.parseInt(userId.replace("user-", ""));
        } catch (NumberFormatException e) {
            throw new ValidationException("userId", userId, 
                    "Invalid user ID format", e);
        }
        
        // Step 3: Simulate database operation
        try {
            saveToDatabase(userId, email);
        } catch (java.sql.SQLException e) {
            throw new BusinessException("REG_001", 
                    "Failed to save user to database", e);
        }
    }
    
    private static void saveToDatabase(String userId, String email) 
            throws java.sql.SQLException {
        // Simulate database error
        throw new java.sql.SQLException("Connection timeout", "08S01", 0);
    }
}
