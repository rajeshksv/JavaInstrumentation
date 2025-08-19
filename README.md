# ByteBuddy Method Instrumentation Examples

This project demonstrates how to use ByteBuddy to instrument method entry and exit using `@Advice.OnMethodEnter` and `@Advice.OnMethodExit` annotations.

## Overview

ByteBuddy is a code generation library for creating Java classes during the runtime of a Java application. This project shows how to:

1. **Basic Method Instrumentation**: Simple method entry and exit logging
2. **Advanced Method Instrumentation**: Timing, statistics, and sophisticated logging
3. **Java Agent Integration**: Using ByteBuddy as a Java agent

## Project Structure

```
src/main/java/com/example/bytebuddy/
├── MethodInstrumentation.java      # Basic instrumentation with Java agent support
├── AdvancedMethodInstrumentation.java  # Advanced instrumentation with timing
├── SampleTargetClass.java          # Target class to be instrumented
├── InstrumentationDemo.java        # Basic demo
└── AdvancedDemo.java               # Advanced demo with statistics
```

## Key Components

### 1. MethodInstrumentation.java
Basic instrumentation class that demonstrates:
- `@Advice.OnMethodEnter`: Logs method entry with class, method name, and arguments
- `@Advice.OnMethodExit`: Logs method exit with return value or exception
- Java agent premain method for runtime instrumentation

### 2. AdvancedMethodInstrumentation.java
Advanced instrumentation with:
- Method execution timing using `System.nanoTime()`
- Thread-local storage for timing data
- Statistics tracking (call count, total time, average time)
- Formatted output with emojis and detailed information
- Exception handling and return value formatting

### 3. SampleTargetClass.java
Target class with various method types:
- Methods with return values
- Void methods
- Methods with multiple arguments
- Methods that throw exceptions
- Static methods

## Usage Examples

### Basic Instrumentation

```java
// Create instrumented class
Class<?> instrumentedClass = new ByteBuddy()
    .subclass(SampleTargetClass.class)
    .method(ElementMatchers.any())
    .intercept(Advice.to(MethodInstrumentation.MethodAdvice.class))
    .make()
    .load(ClassLoader.getSystemClassLoader())
    .getLoaded();

// Use the instrumented class
SampleTargetClass target = (SampleTargetClass) instrumentedClass
    .getDeclaredConstructor().newInstance();
target.simpleMethod("Hello World");
```

### Advanced Instrumentation

```java
// Create instrumented class with advanced advice
Class<?> instrumentedClass = new ByteBuddy()
    .subclass(SampleTargetClass.class)
    .method(ElementMatchers.any())
    .intercept(Advice.to(AdvancedMethodInstrumentation.class))
    .make()
    .load(ClassLoader.getSystemClassLoader())
    .getLoaded();

// Use and get statistics
SampleTargetClass target = (SampleTargetClass) instrumentedClass
    .getDeclaredConstructor().newInstance();
target.calculateSum(10, 20);
AdvancedMethodInstrumentation.printStatistics();
```

## Running the Examples

### Prerequisites
- Java 8 or higher
- Maven 3.6 or higher

### Build the Project
```bash
mvn clean compile
```

### Run Basic Demo
```bash
mvn clean compile
java -cp "target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" com.example.bytebuddy.InstrumentationDemo
```

### Run Advanced Demo
```bash
mvn clean compile
java -cp "target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" com.example.bytebuddy.AdvancedDemo
```

### Run Simple Test
```bash
mvn clean compile
java -cp "target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" com.example.bytebuddy.SimpleTest
```

## Java Agent Usage

To use as a Java agent, build the JAR and run with:

```bash
# Build the JAR
mvn clean package

# Run with Java agent
java -javaagent:target/bytebuddy-instrumentation-1.0.0.jar -cp target/classes com.example.bytebuddy.SampleTargetClass
```

## Key ByteBuddy Concepts

### @Advice.OnMethodEnter
- Executed before the target method
- Can access method metadata via `@Advice.Origin`
- Can access method arguments via `@Advice.AllArguments`
- Can store data for use in exit advice

### @Advice.OnMethodExit
- Executed after the target method (both normal and exceptional exits)
- Can access return value via `@Advice.Return`
- Can access thrown exception via `@Advice.Thrown`
- Can access data stored in enter advice

### Common Annotations
- `@Advice.Origin`: Provides method and class information
- `@Advice.AllArguments`: Array of all method arguments
- `@Advice.Argument(index)`: Specific argument by index
- `@Advice.Return`: Method return value
- `@Advice.Thrown`: Exception thrown by method

## Output Examples

### Basic Instrumentation Output
```
=== METHOD ENTRY ===
Class: SampleTargetClass
Method: simpleMethod
Arguments: [Hello World]
Timestamp: 1703123456789
===================
Executing simpleMethod with input: Hello World
=== METHOD EXIT ===
Class: SampleTargetClass
Method: simpleMethod
Return Value: Processed: Hello World
Timestamp: 1703123456790
==================
```

### Advanced Instrumentation Output
```
🔵 ENTRY | main | SampleTargetClass.simpleMethod | Args: "Hello World"
Executing simpleMethod with input: Hello World
🔴 EXIT  | main | SampleTargetClass.simpleMethod | ✅ Return: "Processed: Hello World" | Duration: 1.23 ms

📊 METHOD STATISTICS:
=====================
SampleTargetClass.simpleMethod | Calls: 3 | Avg Time: 1.15 ms
SampleTargetClass.calculateSum | Calls: 3 | Avg Time: 0.89 ms
```

## Best Practices

1. **Performance**: Use ThreadLocal for storing timing data between enter and exit
2. **Thread Safety**: Use concurrent collections for statistics in multi-threaded environments
3. **Memory Management**: Clear ThreadLocal data in exit advice to prevent memory leaks
4. **Exception Handling**: Always handle exceptions in exit advice
5. **Selective Instrumentation**: Use ElementMatchers to instrument only specific methods/classes

## Dependencies

- ByteBuddy Core: `net.bytebuddy:byte-buddy`
- ByteBuddy Agent: `net.bytebuddy:byte-buddy-agent`
- ByteBuddy JVM: `net.bytebuddy:byte-buddy-jvm`
- Logging: SLF4J and Logback

## License

This project is for educational purposes. ByteBuddy is licensed under the Apache License 2.0.
