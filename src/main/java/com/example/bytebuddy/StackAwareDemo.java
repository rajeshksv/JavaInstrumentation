package com.example.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Advanced demo showing call stack tracking with ByteBuddy instrumentation
 */
public class StackAwareDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Stack-Aware ByteBuddy Instrumentation Demo ===\n");

        // Create instrumented class using stack-aware advice
        Class<?> instrumentedClass = new ByteBuddy()
                .subclass(NestedTargetClass.class)
                .method(ElementMatchers.any())
                .intercept(Advice.to(StackAwareInstrumentation.class))
                .make()
                .load(StackAwareDemo.class.getClassLoader())
                .getLoaded();

        // Create instance of instrumented class
        NestedTargetClass target = (NestedTargetClass) instrumentedClass.getDeclaredConstructor().newInstance();

        System.out.println("=== Testing Simple Nested Method Calls ===\n");
        
        // Test simple nested method calls
        target.processData("methodargument123");
        
        // System.out.println("\n=== Testing Complex Workflow ===\n");
        
        // // Test complex workflow with multiple nested calls
        // target.complexWorkflow();
        
        // System.out.println("\n=== Testing Static Method ===\n");
        
        // // Test static method (note: static methods won't be instrumented by subclass approach)
        // NestedTargetClass.staticMethod();
        
        // System.out.println("\n=== Testing Direct Method Calls ===\n");
        
        // // Test some direct method calls to show call stack depth
        // System.out.println("Current stack depth before calls: " + StackAwareInstrumentation.getCurrentStackDepth());
        
        // // Create another instance to test call stack isolation
        // NestedTargetClass target2 = (NestedTargetClass) instrumentedClass.getDeclaredConstructor().newInstance();
        // target2.processData("isolated123");
        
        // System.out.println("Current stack depth after calls: " + StackAwareInstrumentation.getCurrentStackDepth());
        
        // // Print final statistics
        // StackAwareInstrumentation.printStatistics();
        
        // System.out.println("\n=== Demo Complete ===");
    }
}
