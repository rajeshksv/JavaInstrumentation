package com.example.bytebuddy;

/**
 * Target class with nested method calls to demonstrate call stack tracking
 */
public class NestedTargetClass {

    public void processData(String data) {
        System.out.println("Processing data: " + data);
        
        // Call nested methods
        validateData(data);
        //String processed = transformData(data);
        //saveData(processed);
        
        System.out.println("Data processing completed");
    }

    public void validateData(String data) {
        System.out.println("Validating data: " + data);
        
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        
        // Call deeper nested method
        performDeepValidation(data);
        
        System.out.println("Data validation passed");
    }

    public void performDeepValidation(String data) {
        System.out.println("Performing deep validation: " + data);
        
        // Simulate some validation logic
        if (data.length() < 3) {
            throw new IllegalArgumentException("Data too short");
        }
        
        // Call another nested method
        checkDataFormat(data);
        
        System.out.println("Deep validation completed");
    }

    public void checkDataFormat(String data) {
        System.out.println("Checking data format: " + data);
        
        // Simulate format checking
        if (!data.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("Invalid data format");
        }
        
        System.out.println("Data format is valid");
    }

    private String transformData(String data) {
        System.out.println("Transforming data: " + data);
        
        // Call nested transformation methods
        String upperCase = convertToUpperCase(data);
        String reversed = reverseString(upperCase);
        
        System.out.println("Data transformation completed");
        return reversed;
    }

    private String convertToUpperCase(String data) {
        System.out.println("Converting to uppercase: " + data);
        return data.toUpperCase();
    }

    private String reverseString(String data) {
        System.out.println("Reversing string: " + data);
        return new StringBuilder(data).reverse().toString();
    }

    private void saveData(String data) {
        System.out.println("Saving data: " + data);
        
        // Call nested save methods
        prepareForSave(data);
        writeToStorage(data);
        logSaveOperation(data);
        
        System.out.println("Data saved successfully");
    }

    private void prepareForSave(String data) {
        System.out.println("Preparing data for save: " + data);
        
        // Call deeper nested method
        encryptData(data);
        
        System.out.println("Data prepared for save");
    }

    private void encryptData(String data) {
        System.out.println("Encrypting data: " + data);
        
        // Simulate encryption
        String encrypted = "ENCRYPTED_" + data;
        System.out.println("Data encrypted: " + encrypted);
    }

    private void writeToStorage(String data) {
        System.out.println("Writing to storage: " + data);
        
        // Simulate storage write
        System.out.println("Data written to storage");
    }

    private void logSaveOperation(String data) {
        System.out.println("Logging save operation: " + data);
        
        // Simulate logging
        System.out.println("Save operation logged");
    }

    public void complexWorkflow() {
        System.out.println("Starting complex workflow");
        
        try {
            // Call multiple methods that will create a deep call stack
            processData("test123");
            processData("sample456");
            
            // Call a method that throws an exception
            processData("");
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
        
        System.out.println("Complex workflow completed");
    }

    public static void staticMethod() {
        System.out.println("Static method called");
        
        // Create instance and call instance method
        NestedTargetClass instance = new NestedTargetClass();
        instance.processData("staticTest");
        
        System.out.println("Static method completed");
    }
}
