package com.example.bytebuddy;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Advanced ByteBuddy instrumentation example with timing and custom logic
 */
public class AdvancedMethodInstrumentation {

    // Thread-local storage for method entry timestamps
    public static final ThreadLocal<Long> methodStartTime = new ThreadLocal<>();
    
    // Statistics tracking
    public static final ConcurrentHashMap<String, AtomicLong> methodCallCount = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, AtomicLong> methodTotalTime = new ConcurrentHashMap<>();

    /**
     * Advanced method entry advice with timing and statistics
     */
    @Advice.OnMethodEnter
    public static void onMethodEnter(
            @Advice.Origin String method,
            @Advice.Origin Class<?> clazz,
            @Advice.AllArguments Object[] arguments) {
        
        // Record start time
        long startTime = System.nanoTime();
        methodStartTime.set(startTime);
        
        // Update call count (simplified)
        String methodKey = clazz.getSimpleName() + "." + method;
        AtomicLong count = methodCallCount.get(methodKey);
        if (count == null) {
            count = new AtomicLong(0);
            methodCallCount.put(methodKey, count);
        }
        count.incrementAndGet();
        
        // Log method entry with thread info
        System.out.println("🔵 ENTRY | " + 
                Thread.currentThread().getName() + " | " +
                clazz.getSimpleName() + "." + method + 
                " | Args: " + formatArguments(arguments));
    }

            /**
         * Advanced method exit advice with timing and statistics
         */
        @Advice.OnMethodExit
        public static void onMethodExit(
                @Advice.Origin String method,
                @Advice.Origin Class<?> clazz,
                @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue) {
        
        // Calculate execution time
        Long startTime = methodStartTime.get();
        long endTime = System.nanoTime();
        long duration = startTime != null ? endTime - startTime : 0;
        
        // Update total time (simplified)
        String methodKey = clazz.getSimpleName() + "." + method;
        AtomicLong totalTime = methodTotalTime.get(methodKey);
        if (totalTime == null) {
            totalTime = new AtomicLong(0);
            methodTotalTime.put(methodKey, totalTime);
        }
        totalTime.addAndGet(duration);
        
        // Clear thread local
        methodStartTime.remove();
        
        // Format return value
        String returnInfo = "✅ Return: " + formatReturnValue(returnValue);
        
        // Log method exit with timing
        System.out.println("🔴 EXIT  | " + 
                Thread.currentThread().getName() + " | " +
                clazz.getSimpleName() + "." + method + 
                " | " + returnInfo + 
                " | Duration: " + formatDuration(duration));
    }

    /**
     * Utility method to format method arguments
     */
    public static String formatArguments(Object[] arguments) {
        if (arguments == null || arguments.length == 0) {
            return "none";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(formatValue(arguments[i]));
        }
        return sb.toString();
    }

    /**
     * Utility method to format return value
     */
    public static String formatReturnValue(Object value) {
        if (value == null) {
            return "null";
        }
        return formatValue(value);
    }

    /**
     * Utility method to format any value
     */
    public static String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        
        // For complex objects, show class name and hash code
        return value.getClass().getSimpleName() + "@" + Integer.toHexString(value.hashCode());
    }

    /**
     * Utility method to format duration in nanoseconds
     */
    public static String formatDuration(long nanoseconds) {
        if (nanoseconds < 1000) {
            return nanoseconds + " ns";
        } else if (nanoseconds < 1_000_000) {
            return String.format("%.2f μs", nanoseconds / 1000.0);
        } else if (nanoseconds < 1_000_000_000) {
            return String.format("%.2f ms", nanoseconds / 1_000_000.0);
        } else {
            return String.format("%.2f s", nanoseconds / 1_000_000_000.0);
        }
    }

    /**
     * Print method statistics
     */
    public static void printStatistics() {
        System.out.println("\n📊 METHOD STATISTICS:");
        System.out.println("=====================");
        
        for (java.util.Map.Entry<String, AtomicLong> entry : methodCallCount.entrySet()) {
            String method = entry.getKey();
            AtomicLong count = entry.getValue();
            AtomicLong totalTime = methodTotalTime.get(method);
            long avgTime = totalTime != null ? totalTime.get() / count.get() : 0;
            
            System.out.printf("%s | Calls: %d | Avg Time: %s%n", 
                    method, count.get(), formatDuration(avgTime));
        }
    }
}
