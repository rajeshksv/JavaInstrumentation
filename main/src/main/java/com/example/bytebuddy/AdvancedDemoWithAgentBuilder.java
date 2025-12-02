package com.example.bytebuddy;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

/**
 * Advanced demo showing sophisticated ByteBuddy instrumentation using AgentBuilder
 * This demonstrates how to use AgentBuilder for runtime class transformation
 */
public class AdvancedDemoWithAgentBuilder {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Advanced ByteBuddy Instrumentation Demo (AgentBuilder) ===\n");

        // Install ByteBuddy agent to get Instrumentation instance
        Instrumentation instrumentation = ByteBuddyAgent.install();
        
        // Setup AgentBuilder to instrument SampleTargetClass
        // This will transform the class when it's loaded or retransform it if already loaded
        new AgentBuilder.Default()
                .type(ElementMatchers.named("com.example.bytebuddy.SampleTargetClass"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                    System.out.println("🔄 Transforming class: " + typeDescription.getName());
                    return builder.method(ElementMatchers.any())
                            .intercept(Advice.to(AdvancedMethodInstrumentation.class));
                })
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .installOn(instrumentation);

        // Force retransformation if class is already loaded
        // This is needed because the class might be loaded before agent installation
        try {
            Class<?> targetClass = Class.forName("com.example.bytebuddy.SampleTargetClass", false, 
                    Thread.currentThread().getContextClassLoader());
            if (instrumentation.isModifiableClass(targetClass)) {
                instrumentation.retransformClasses(targetClass);
                System.out.println("✓ Retransformed SampleTargetClass (if already loaded)\n");
            }
        } catch (ClassNotFoundException e) {
            // Class not loaded yet - AgentBuilder will handle it on first load
            System.out.println("✓ AgentBuilder will instrument SampleTargetClass on first load\n");
        } catch (Exception e) {
            System.out.println("⚠ Warning: " + e.getMessage() + "\n");
        }

        // Load and create instance - AgentBuilder will intercept if not already loaded
        Class<?> targetClass = Class.forName("com.example.bytebuddy.SampleTargetClass");
        Object targetObj = targetClass.getDeclaredConstructor().newInstance();
        SampleTargetClass target = (SampleTargetClass) targetObj;

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

        // Test static method (with AgentBuilder, static methods can be instrumented)
        System.out.println("--- Static Method Test ---");
        SampleTargetClass.staticMethod("static test");
        System.out.println();

        // Print final statistics
        AdvancedMethodInstrumentation.printStatistics();

        System.out.println("\n=== Advanced Demo Complete ===");
    }
}

