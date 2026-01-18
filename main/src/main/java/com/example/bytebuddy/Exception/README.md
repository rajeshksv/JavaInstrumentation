# Exception Examples

This package contains comprehensive examples demonstrating various aspects of exception handling in Java.

## Files Overview

### Custom Exception Classes

1. **CustomCheckedException.java**
   - Example of a custom checked exception
   - Must be declared in method signatures or caught
   - Extends `Exception`

2. **CustomUncheckedException.java**
   - Example of a custom unchecked exception
   - Does not need to be declared
   - Extends `RuntimeException`

3. **ValidationException.java**
   - Domain-specific custom exception for validation errors
   - Includes field name and invalid value information
   - Extends `RuntimeException`

4. **BusinessException.java**
   - Business logic exception with error codes
   - Extends `Exception` (checked)
   - Includes error code for categorization

### Example Classes

5. **ExceptionOccurrenceExamples.java**
   - Demonstrates how various exceptions occur
   - Examples include:
     - NullPointerException
     - ArrayIndexOutOfBoundsException
     - NumberFormatException
     - ArithmeticException
     - FileNotFoundException
     - And more...
   - Run: `ExceptionOccurrenceExamples.main()`

6. **ExceptionCatchingExamples.java**
   - Demonstrates various ways to catch exceptions
   - Examples include:
     - Basic try-catch
     - Multiple catch blocks
     - Multi-catch (Java 7+)
     - Try-catch-finally
     - Try-with-resources
     - Nested try-catch
     - Suppressed exceptions
   - Run: `ExceptionCatchingExamples.main()`

7. **ExceptionChainingExamples.java**
   - Demonstrates exception chaining
   - Examples include:
     - Basic exception chaining
     - Multiple level chaining
     - Chaining with custom exceptions
     - Chaining checked and unchecked exceptions
     - SQLException chaining
     - Extracting root cause
   - Run: `ExceptionChainingExamples.main()`

8. **ExceptionLoggingExamples.java**
   - Demonstrates proper exception logging with stack traces
   - Examples include:
     - Basic exception logging
     - Logging with different log levels
     - Logging with context information
     - Logging chained exceptions
     - Converting stack trace to string
     - Logging stack trace elements
   - Run: `ExceptionLoggingExamples.main()`

9. **ComprehensiveExceptionDemo.java**
   - Comprehensive demonstration combining all concepts
   - Real-world scenario example
   - Best practices for exception handling
   - **Main entry point** - Run this to see all examples
   - Run: `ComprehensiveExceptionDemo.main()`

## Running the Examples

### Option 1: Run Comprehensive Demo (Recommended)
```bash
# From the project root
mvn exec:java -Dexec.mainClass="com.example.bytebuddy.Exception.ComprehensiveExceptionDemo"
```

### Option 2: Run Individual Examples
```bash
# Exception Occurrence Examples
mvn exec:java -Dexec.mainClass="com.example.bytebuddy.Exception.ExceptionOccurrenceExamples"

# Exception Catching Examples
mvn exec:java -Dexec.mainClass="com.example.bytebuddy.Exception.ExceptionCatchingExamples"

# Exception Chaining Examples
mvn exec:java -Dexec.mainClass="com.example.bytebuddy.Exception.ExceptionChainingExamples"

# Exception Logging Examples
mvn exec:java -Dexec.mainClass="com.example.bytebuddy.Exception.ExceptionLoggingExamples"
```

## Key Concepts Demonstrated

### 1. How Exceptions Happen
- Common runtime exceptions (NullPointerException, ArrayIndexOutOfBoundsException, etc.)
- Checked exceptions (FileNotFoundException, IOException, etc.)
- Errors (OutOfMemoryError, StackOverflowError)

### 2. Exception Catching
- Basic try-catch blocks
- Multiple catch blocks (order matters)
- Multi-catch (Java 7+)
- Finally blocks
- Try-with-resources (automatic resource management)
- Nested try-catch

### 3. Custom Exceptions
- Creating checked exceptions
- Creating unchecked exceptions
- Domain-specific exceptions
- Exception constructors (message, cause)

### 4. Exception Chaining
- Preserving original exceptions
- Multiple levels of chaining
- Using `initCause()`
- Extracting root cause
- Traversing exception chains

### 5. Exception Logging
- Logging with full stack traces
- Logging with context information
- Logging chained exceptions
- Different log levels for exceptions
- Converting stack traces to strings
- Logging stack trace elements

## Best Practices Demonstrated

1. **Always log exceptions with stack traces**: Use `logger.error(message, exception)` format
2. **Include context**: Add relevant information (user ID, operation, etc.) to log messages
3. **Preserve exception chains**: Use exception chaining to maintain original exception information
4. **Use appropriate log levels**: Error for serious issues, Warn for recoverable issues
5. **Clean up resources**: Use try-with-resources or finally blocks
6. **Create domain-specific exceptions**: Use custom exceptions for better error categorization

## Logging Configuration

The examples use SLF4J with Logback. Make sure `logback.xml` is configured in your classpath to see the formatted log output with stack traces.

## Notes

- All examples are designed to demonstrate exception handling concepts
- Some examples intentionally throw exceptions to show how they work
- The code follows Java 8 compatibility (no Java 11+ features)
- All stack traces are logged using SLF4J logger for proper formatting
