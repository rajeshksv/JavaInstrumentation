package com.example.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Simple test to verify ByteBuddy instrumentation works
 */
public class SimpleTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Simple ByteBuddy Test");
        System.out.println("=====================\n");

        // Create instrumented class
        Class<?> instrumentedClass = new ByteBuddy()
                .subclass(SampleTargetClass.class)
                .method(ElementMatchers.any())
                .intercept(Advice.to(MethodInstrumentation.MethodAdvice.class))
                .make()
                .load(SimpleTest.class.getClassLoader())
                .getLoaded();

        // Create instance and test
        SampleTargetClass target = (SampleTargetClass) instrumentedClass.getDeclaredConstructor().newInstance();
        
        System.out.println("Testing simple method:");
        String result = target.simpleMethod("test");
        System.out.println("Result: " + result);
        
        System.out.println("\nTesting calculation method:");
        int sum = target.calculateSum(5, 10);
        System.out.println("Sum: " + sum);
        
        System.out.println("\nTest completed successfully!");
    }
}
