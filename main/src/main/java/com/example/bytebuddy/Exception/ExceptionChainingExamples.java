package com.example.bytebuddy.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Examples demonstrating exception chaining
 * Exception chaining allows you to preserve the original exception while
 * throwing a new exception that provides more context
 */
public class ExceptionChainingExamples {
    
    private static final Logger logger = LoggerFactory.getLogger(ExceptionChainingExamples.class);
    
    /**
     * Example 1: Basic exception chaining with cause
     */
    public static void basicExceptionChaining() {
        logger.info("=== Basic exception chaining example ===");
        try {
            performDatabaseOperation();
        } catch (BusinessException e) {
            logger.error("BusinessException occurred: {}", e.getMessage(), e);
            logger.error("Root cause: {}", e.getCause() != null ? e.getCause().getMessage() : "None");
            logger.error("Full exception chain:", e);
        }
    }
    
    /**
     * Example 2: Multiple levels of exception chaining
     */
    public static void multipleLevelChaining() {
        logger.info("=== Multiple level exception chaining example ===");
        try {
            processUserData();
        } catch (BusinessException e) {
            logger.error("Top level exception: {}", e.getMessage(), e);
            
            // Traverse the exception chain
            Throwable current = e;
            int level = 0;
            while (current != null) {
                logger.error("Level {}: {} - {}", level, 
                        current.getClass().getSimpleName(), current.getMessage());
                current = current.getCause();
                level++;
            }
        }
    }
    
    /**
     * Example 3: Chaining with custom exceptions
     */
    public static void customExceptionChaining() {
        logger.info("=== Custom exception chaining example ===");
        try {
            validateAndProcess();
        } catch (ValidationException e) {
            logger.error("ValidationException: {}", e.getMessage(), e);
            if (e.getCause() != null) {
                logger.error("Caused by: {}", e.getCause().getClass().getSimpleName());
                logger.error("Cause message: {}", e.getCause().getMessage());
            }
        }
    }
    
    /**
     * Example 4: Chaining checked and unchecked exceptions
     */
    public static void checkedUncheckedChaining() {
        logger.info("=== Checked/Unchecked exception chaining example ===");
        try {
            readConfigurationFile();
        } catch (CustomUncheckedException e) {
            logger.error("Unchecked exception: {}", e.getMessage(), e);
            if (e.getCause() instanceof IOException) {
                logger.error("Root cause is IOException: {}", e.getCause().getMessage());
            }
        }
    }
    
    /**
     * Example 5: Exception chaining with SQLException
     */
    public static void sqlExceptionChaining() {
        logger.info("=== SQLException chaining example ===");
        try {
            executeDatabaseQuery();
        } catch (BusinessException e) {
            logger.error("BusinessException: {}", e.getMessage(), e);
            
            // SQLException can have a chain of exceptions
            if (e.getCause() instanceof SQLException) {
                SQLException sqlEx = (SQLException) e.getCause();
                logger.error("SQLException SQLState: {}", sqlEx.getSQLState());
                logger.error("SQLException ErrorCode: {}", sqlEx.getErrorCode());
                
                // SQLException can have next exception
                SQLException next = sqlEx.getNextException();
                if (next != null) {
                    logger.error("Next SQLException: {}", next.getMessage());
                }
            }
        }
    }
    
    /**
     * Example 6: Preserving stack trace in chained exceptions
     */
    public static void preserveStackTraceChaining() {
        logger.info("=== Preserving stack trace in chained exceptions ===");
        try {
            complexOperation();
        } catch (CustomUncheckedException e) {
            logger.error("Exception with preserved stack trace:", e);
            
            // Print original exception stack trace
            if (e.getCause() != null) {
                logger.error("Original exception stack trace:");
                e.getCause().printStackTrace();
            }
        }
    }
    
    /**
     * Example 7: Exception chaining with multiple causes (using initCause)
     */
    public static void initCauseChaining() {
        logger.info("=== Exception chaining using initCause ===");
        try {
            operationWithInitCause();
        } catch (IllegalStateException e) {
            logger.error("IllegalStateException: {}", e.getMessage(), e);
            logger.error("Cause set via initCause: {}", 
                    e.getCause() != null ? e.getCause().getMessage() : "None");
        }
    }
    
    /**
     * Example 8: Extracting root cause from exception chain
     */
    public static void extractRootCause() {
        logger.info("=== Extracting root cause from exception chain ===");
        try {
            nestedOperation();
        } catch (Exception e) {
            Throwable rootCause = getRootCause(e);
            logger.error("Top level exception: {}", e.getClass().getSimpleName());
            logger.error("Root cause: {}", rootCause.getClass().getSimpleName());
            logger.error("Root cause message: {}", rootCause.getMessage());
            logger.error("Full chain:", e);
        }
    }
    
    // Helper methods that throw chained exceptions
    
    private static void performDatabaseOperation() throws BusinessException {
        try {
            // Simulate database operation that throws SQLException
            throw new SQLException("Connection timeout", "08S01", 0);
        } catch (SQLException e) {
            throw new BusinessException("DB_001", "Failed to perform database operation", e);
        }
    }
    
    private static void processUserData() throws BusinessException {
        try {
            validateUserInput();
        } catch (ValidationException e) {
            throw new BusinessException("VAL_001", "User data processing failed", e);
        }
    }
    
    private static void validateUserInput() throws ValidationException {
        try {
            parseUserData();
        } catch (NumberFormatException e) {
            throw new ValidationException("userId", "123abc", 
                    "Invalid user ID format", e);
        }
    }
    
    private static void parseUserData() {
        Integer.parseInt("invalid");
    }
    
    private static void validateAndProcess() throws ValidationException {
        try {
            FileInputStream fis = new FileInputStream("config.txt");
            fis.close();
        } catch (FileNotFoundException e) {
            throw new ValidationException("configFile", "config.txt", 
                    "Configuration file not found", e);
        } catch (IOException e) {
            throw new ValidationException("configFile", "config.txt", 
                    "Error reading configuration file", e);
        }
    }
    
    private static void readConfigurationFile() {
        try {
            FileInputStream fis = new FileInputStream("missing.txt");
            fis.close();
        } catch (IOException e) {
            throw new CustomUncheckedException("Failed to read configuration", e);
        }
    }
    
    private static void executeDatabaseQuery() throws BusinessException {
        try {
            // Simulate SQLException with next exception
            SQLException sqlEx1 = new SQLException("First error", "23000", 1);
            SQLException sqlEx2 = new SQLException("Second error", "23000", 2);
            sqlEx1.setNextException(sqlEx2);
            throw sqlEx1;
        } catch (SQLException e) {
            throw new BusinessException("DB_002", "Query execution failed", e);
        }
    }
    
    private static void complexOperation() {
        try {
            innerOperation();
        } catch (IllegalArgumentException e) {
            throw new CustomUncheckedException("Complex operation failed", e);
        }
    }
    
    private static void innerOperation() {
        throw new IllegalArgumentException("Invalid argument in inner operation");
    }
    
    private static void operationWithInitCause() {
        try {
            throw new IOException("I/O error occurred");
        } catch (IOException e) {
            IllegalStateException ise = new IllegalStateException("Operation failed");
            ise.initCause(e);
            throw ise;
        }
    }
    
    private static void nestedOperation() {
        try {
            level1();
        } catch (Exception e) {
            throw new RuntimeException("Level 0 exception", e);
        }
    }
    
    private static void level1() throws Exception {
        try {
            level2();
        } catch (Exception e) {
            throw new Exception("Level 1 exception", e);
        }
    }
    
    private static void level2() throws Exception {
        try {
            level3();
        } catch (Exception e) {
            throw new Exception("Level 2 exception", e);
        }
    }
    
    private static void level3() throws Exception {
        throw new IllegalArgumentException("Level 3 - root cause");
    }
    
    /**
     * Utility method to extract root cause from exception chain
     */
    private static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable.getCause();
        if (cause == null) {
            return throwable;
        }
        return getRootCause(cause);
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
     * Run all exception chaining examples
     */
    public static void runAllExamples() {
        logger.info("\n" + repeat("=", 80));
        logger.info("EXCEPTION CHAINING EXAMPLES");
        logger.info(repeat("=", 80) + "\n");
        
        basicExceptionChaining();
        multipleLevelChaining();
        customExceptionChaining();
        checkedUncheckedChaining();
        sqlExceptionChaining();
        preserveStackTraceChaining();
        initCauseChaining();
        extractRootCause();
        
        logger.info("\n" + repeat("=", 80));
        logger.info("All exception chaining examples completed");
        logger.info(repeat("=", 80) + "\n");
    }
    
    public static void main(String[] args) {
        runAllExamples();
    }
}
