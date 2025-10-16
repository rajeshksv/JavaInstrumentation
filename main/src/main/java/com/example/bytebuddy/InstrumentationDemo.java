package com.example.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Demo class showing how to use ByteBuddy instrumentation programmatically
 */
public class InstrumentationDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== ByteBuddy Method Instrumentation Demo ===\n");

        // Create instrumented class using ByteBuddy
        Class<?> instrumentedClass = new ByteBuddy()
                .subclass(SampleTargetClass.class)
                .method(ElementMatchers.any())
                .intercept(Advice.to(MethodInstrumentation.MethodAdvice.class))
                .make()
                .load(InstrumentationDemo.class.getClassLoader())
                .getLoaded();

        // Create instance of instrumented class
        SampleTargetClass target = (SampleTargetClass) instrumentedClass.getDeclaredConstructor().newInstance();

        System.out.println("Testing various methods with instrumentation:\n");

        // Test 1: Simple method
        System.out.println("1. Testing simpleMethod:");
        String result1 = target.simpleMethod("Hello World");
        System.out.println("Final result: " + result1 + "\n");

        // Test 2: Method with return value
        System.out.println("2. Testing calculateSum:");
        int result2 = target.calculateSum(10, 20);
        System.out.println("Final result: " + result2 + "\n");

        // Test 3: Void method
        System.out.println("3. Testing voidMethod:");
        target.voidMethod();
        System.out.println();

        // Test 4: Method with multiple arguments
        System.out.println("4. Testing methodWithMultipleArgs:");
        String result4 = target.methodWithMultipleArgs("test", 42, true);
        System.out.println("Final result: " + result4 + "\n");

        // Test 5: Method that throws exception
        System.out.println("5. Testing methodWithException:");
        try {
            target.methodWithException();
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
        System.out.println();

        // Test 6: Static method
        System.out.println("6. Testing static method:");
        String staticResult = SampleTargetClass.staticMethod("hello");
        System.out.println("Final result: " + staticResult + "\n");

        System.out.println("=== Demo Complete ===");
    }
}
