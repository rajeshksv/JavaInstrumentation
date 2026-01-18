package com.example.bytebuddy.Exception;

/**
 * Example of a domain-specific custom exception
 * Used for validation errors
 */
public class ValidationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final String fieldName;
    private final Object invalidValue;
    
    /**
     * Constructor with field name and invalid value
     */
    public ValidationException(String fieldName, Object invalidValue, String message) {
        super(String.format("Validation failed for field '%s' with value '%s': %s", 
                fieldName, invalidValue, message));
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }
    
    /**
     * Constructor with field name only
     */
    public ValidationException(String fieldName, String message) {
        super(String.format("Validation failed for field '%s': %s", fieldName, message));
        this.fieldName = fieldName;
        this.invalidValue = null;
    }
    
    /**
     * Constructor with chained exception
     */
    public ValidationException(String fieldName, Object invalidValue, String message, Throwable cause) {
        super(String.format("Validation failed for field '%s' with value '%s': %s", 
                fieldName, invalidValue, message), cause);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getInvalidValue() {
        return invalidValue;
    }
}
