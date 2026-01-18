package com.example.bytebuddy.Exception;

/**
 * Example of a business logic exception
 * Used for business rule violations
 */
public class BusinessException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private final String errorCode;
    
    /**
     * Constructor with error code and message
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Constructor with error code, message, and cause
     */
    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        return String.format("BusinessException[errorCode=%s, message=%s]", 
                errorCode, getMessage());
    }
}
