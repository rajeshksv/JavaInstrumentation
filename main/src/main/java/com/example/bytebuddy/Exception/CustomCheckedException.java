package com.example.bytebuddy.Exception;

/**
 * Custom checked exception example
 * Checked exceptions must be declared in method signatures or caught
 */
public class CustomCheckedException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Default constructor
     */
    public CustomCheckedException() {
        super();
    }
    
    /**
     * Constructor with message
     */
    public CustomCheckedException(String message) {
        super(message);
    }
    
    /**
     * Constructor with message and cause (for exception chaining)
     */
    public CustomCheckedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor with cause only
     */
    public CustomCheckedException(Throwable cause) {
        super(cause);
    }
}
