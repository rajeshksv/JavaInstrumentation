package com.example.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;

/**
 * ByteBuddy instrumentation example demonstrating method entry and exit instrumentation
 * using @Advice.OnMethodEnter and @Advice.OnMethodExit annotations.
 */
public class MethodInstrumentation {

    /**
     * Premain method for Java agent
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("ByteBuddy Method Instrumentation Agent Starting...");
        
        new AgentBuilder.Default()
                .type(ElementMatchers.nameStartsWith("com.example.bytebuddy"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.method(ElementMatchers.any())
                                .intercept(Advice.to(MethodAdvice.class)))
                .installOn(inst);
    }

    /**
     * Advice class containing the method entry and exit logic
     */
    public static class MethodAdvice {

        /**
         * Method entry advice - executed before the target method
         */
        @Advice.OnMethodEnter
        public static void onMethodEnter(
                @Advice.Origin String method,
                @Advice.Origin Class<?> clazz,
                @Advice.AllArguments Object[] arguments) {
            
            System.out.println("=== METHOD ENTRY ===");
            System.out.println("Class: " + clazz.getSimpleName());
            System.out.println("Method: " + method);
            System.out.println("Arguments: " + java.util.Arrays.toString(arguments));
            System.out.println("Timestamp: " + System.currentTimeMillis());
            System.out.println("===================");
        }

        /**
         * Method exit advice - executed after the target method
         */
        @Advice.OnMethodExit
        public static void onMethodExit(
                @Advice.Origin String method,
                @Advice.Origin Class<?> clazz,
                @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue) {
            
            System.out.println("=== METHOD EXIT ===");
            System.out.println("Class: " + clazz.getSimpleName());
            System.out.println("Method: " + method);
            System.out.println("Return Value: " + returnValue);
            System.out.println("Timestamp: " + System.currentTimeMillis());
            System.out.println("==================");
        }
    }
}
