package com.example.bytebuddy.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Examples demonstrating how various exceptions occur
 */
public class ExceptionOccurrenceExamples {
    
    private static final Logger logger = LoggerFactory.getLogger(ExceptionOccurrenceExamples.class);
    
    /**
     * Example 1: NullPointerException
     * Occurs when trying to access methods/fields on null reference
     */
    public static void demonstrateNullPointerException() {
        logger.info("=== Demonstrating NullPointerException ===");
        try {
            String str = null;
            int length = str.length(); // This will throw NullPointerException
        } catch (NullPointerException e) {
            logger.error("NullPointerException occurred: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 2: ArrayIndexOutOfBoundsException
     * Occurs when accessing array with invalid index
     */
    public static void demonstrateArrayIndexOutOfBoundsException() {
        logger.info("=== Demonstrating ArrayIndexOutOfBoundsException ===");
        try {
            int[] array = {1, 2, 3};
            int value = array[10]; // This will throw ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("ArrayIndexOutOfBoundsException occurred: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 3: IndexOutOfBoundsException (List)
     * Occurs when accessing list with invalid index
     */
    public static void demonstrateIndexOutOfBoundsException() {
        logger.info("=== Demonstrating IndexOutOfBoundsException ===");
        try {
            List<String> list = new ArrayList<>();
            list.add("item1");
            String item = list.get(5); // This will throw IndexOutOfBoundsException
        } catch (IndexOutOfBoundsException e) {
            logger.error("IndexOutOfBoundsException occurred: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 4: NumberFormatException
     * Occurs when trying to parse invalid number string
     */
    public static void demonstrateNumberFormatException() {
        logger.info("=== Demonstrating NumberFormatException ===");
        try {
            String invalidNumber = "abc123";
            int number = Integer.parseInt(invalidNumber); // This will throw NumberFormatException
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException occurred: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 5: ArithmeticException
     * Occurs when performing invalid arithmetic operations
     */
    public static void demonstrateArithmeticException() {
        logger.info("=== Demonstrating ArithmeticException ===");
        try {
            int result = 10 / 0; // This will throw ArithmeticException
        } catch (ArithmeticException e) {
            logger.error("ArithmeticException occurred: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 6: ClassCastException
     * Occurs when trying to cast object to incompatible type
     */
    public static void demonstrateClassCastException() {
        logger.info("=== Demonstrating ClassCastException ===");
        try {
            Object obj = new Integer(10);
            String str = (String) obj; // This will throw ClassCastException
        } catch (ClassCastException e) {
            logger.error("ClassCastException occurred: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 7: IllegalArgumentException
     * Occurs when method receives illegal argument
     */
    public static void demonstrateIllegalArgumentException() {
        logger.info("=== Demonstrating IllegalArgumentException ===");
        try {
            List<String> list = new ArrayList<>();
            list.add("item1");
            list.add("item2");
            list.subList(2, 1); // This will throw IllegalArgumentException (from > to)
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException occurred: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 8: FileNotFoundException (Checked Exception)
     * Occurs when trying to access non-existent file
     */
    public static void demonstrateFileNotFoundException() {
        logger.info("=== Demonstrating FileNotFoundException ===");
        try {
            FileInputStream fis = new FileInputStream("non_existent_file.txt");
            fis.close();
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException occurred: {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error("IOException occurred: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 9: OutOfMemoryError
     * Occurs when JVM runs out of memory
     * Note: This is an Error, not an Exception, but demonstrates error handling
     */
    public static void demonstrateOutOfMemoryError() {
        logger.info("=== Demonstrating OutOfMemoryError (simulated) ===");
        try {
            // This might throw OutOfMemoryError if heap is too small
            List<byte[]> memoryHog = new ArrayList<>();
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                memoryHog.add(new byte[1024 * 1024]); // 1MB each
            }
        } catch (OutOfMemoryError e) {
            logger.error("OutOfMemoryError occurred: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 10: StackOverflowError
     * Occurs when stack overflow happens (usually infinite recursion)
     */
    public static void demonstrateStackOverflowError() {
        logger.info("=== Demonstrating StackOverflowError ===");
        try {
            infiniteRecursion();
        } catch (StackOverflowError e) {
            logger.error("StackOverflowError occurred: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Helper method that causes infinite recursion
     */
    private static void infiniteRecursion() {
        infiniteRecursion(); // Infinite recursion
    }
    
    /**
     * Example 11: StringIndexOutOfBoundsException
     * Occurs when accessing string with invalid index
     */
    public static void demonstrateStringIndexOutOfBoundsException() {
        logger.info("=== Demonstrating StringIndexOutOfBoundsException ===");
        try {
            String str = "Hello";
            char ch = str.charAt(10); // This will throw StringIndexOutOfBoundsException
        } catch (StringIndexOutOfBoundsException e) {
            logger.error("StringIndexOutOfBoundsException occurred: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 12: IllegalStateException
     * Occurs when object is in invalid state for operation
     */
    public static void demonstrateIllegalStateException() {
        logger.info("=== Demonstrating IllegalStateException ===");
        try {
            List<String> list = new ArrayList<>();
            list.add("item1");
            list.iterator().remove(); // This will throw IllegalStateException (no next() called)
        } catch (IllegalStateException e) {
            logger.error("IllegalStateException occurred: {}", e.getMessage(), e);
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
     * Run all exception occurrence examples
     */
    public static void runAllExamples() {
        logger.info("\n" + repeat("=", 80));
        logger.info("EXCEPTION OCCURRENCE EXAMPLES");
        logger.info(repeat("=", 80) + "\n");
        
        demonstrateNullPointerException();
        demonstrateArrayIndexOutOfBoundsException();
        demonstrateIndexOutOfBoundsException();
        demonstrateNumberFormatException();
        demonstrateArithmeticException();
        demonstrateClassCastException();
        demonstrateIllegalArgumentException();
        demonstrateFileNotFoundException();
        demonstrateStringIndexOutOfBoundsException();
        demonstrateIllegalStateException();
        
        // Commented out to avoid actual crashes
        // demonstrateOutOfMemoryError();
        // demonstrateStackOverflowError();
        
        logger.info("\n" + repeat("=", 80));
        logger.info("All exception occurrence examples completed");
        logger.info(repeat("=", 80) + "\n");
    }
    
    public static void main(String[] args) {
        runAllExamples();
    }
}
