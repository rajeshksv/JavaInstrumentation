package com.example.bytebuddy.Exception;

/**
 * Custom unchecked exception example
 * Unchecked exceptions extend RuntimeException and don't need to be declared
 */
public class CustomUncheckedException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Default constructor
     */
    public CustomUncheckedException() {
        super();
    }
    
    /**
     * Constructor with message
     */
    public CustomUncheckedException(String message) {
        super(message);
    }
    
    /**
     * Constructor with message and cause (for exception chaining)
     */
    public CustomUncheckedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor with cause only
     */
    public CustomUncheckedException(Throwable cause) {
        super(cause);
    }
}
