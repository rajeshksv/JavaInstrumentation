package com.example.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Advanced demo showing sophisticated ByteBuddy instrumentation
 */
public class AdvancedDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Advanced ByteBuddy Instrumentation Demo ===\n");

        // Create instrumented class using advanced advice
        Class<?> instrumentedClass = new ByteBuddy()
                .subclass(SampleTargetClass.class)
                .method(ElementMatchers.any())
                .intercept(Advice.to(AdvancedMethodInstrumentation.class))
                .make()
                .load(AdvancedDemo.class.getClassLoader())
                .getLoaded();

        // Create instance of instrumented class
        SampleTargetClass target = (SampleTargetClass) instrumentedClass.getDeclaredConstructor().newInstance();

        System.out.println("Testing methods with advanced instrumentation:\n");

        // Test multiple calls to show statistics
        for (int i = 0; i < 3; i++) {
            System.out.println("--- Iteration " + (i + 1) + " ---");
            
            // Test simple method
            target.simpleMethod("test" + i);
            
            // Test calculation method
            target.calculateSum(i * 10, i * 20);
            
            // Test void method
            target.voidMethod();
            
            // Test method with multiple arguments
            target.methodWithMultipleArgs("arg" + i, i, i % 2 == 0);
            
            System.out.println();
        }

        // Test exception handling
        System.out.println("--- Exception Test ---");
        try {
            target.methodWithException();
        } catch (Exception e) {
            System.out.println("Exception caught in main: " + e.getMessage());
        }
        System.out.println();

        // Test static method (note: static methods won't be instrumented by subclass approach)
        System.out.println("--- Static Method Test ---");
        SampleTargetClass.staticMethod("static test");
        System.out.println();

        // Print final statistics
        AdvancedMethodInstrumentation.printStatistics();

        System.out.println("\n=== Advanced Demo Complete ===");
    }
}
