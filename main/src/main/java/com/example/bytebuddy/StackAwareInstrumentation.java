package com.example.bytebuddy;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Stack;

/**
 * Advanced ByteBuddy instrumentation with call stack tracking
 */
public class StackAwareInstrumentation {

    // Thread-local storage for call stack
    public static final ThreadLocal<Stack<MethodCallInfo>> callStack = new ThreadLocal<>();
    
    
    // Thread-local storage for method entry timestamps
    public static final ThreadLocal<Long> methodStartTime = new ThreadLocal<>();
    
    // Statistics tracking
    public static final ConcurrentHashMap<String, AtomicLong> methodCallCount = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, AtomicLong> methodTotalTime = new ConcurrentHashMap<>();

    // Hardcoded, but can be set at entry points
    public static final ThreadLocal<String> requestStack = ThreadLocal.withInitial(() -> "FK Plus User");
    public static final ThreadLocal<String> flowStack = ThreadLocal.withInitial(() -> "Flow1");


    /**
     * Method call information
     */
    public static class MethodCallInfo {
        public final String className;
        public final String methodName;
        public final String fullMethodSignature;
        public final long startTime;
        public final int depth;

        public MethodCallInfo(String className, String methodName, String fullMethodSignature, long startTime, int depth) {
            this.className = className;
            this.methodName = methodName;
            this.fullMethodSignature = fullMethodSignature;
            this.startTime = startTime;
            this.depth = depth;
        }

        public MethodCallInfo(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
            this.fullMethodSignature = "";
            this.startTime = 0;
            this.depth = 0;
        }

        public String getDisplayName() {
            return className + "." + methodName;
        }

        public String getIndentation() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                sb.append("  ");
            }
            return sb.toString();
        }
    }

    /**
     * Advanced method entry advice with call stack tracking
     */
    @Advice.OnMethodEnter
    public static void onMethodEnter(
            @Advice.Origin String method,
            @Advice.Origin Class<?> clazz,
            @Advice.AllArguments Object[] arguments) {
        
        // Initialize call stack if needed
        Stack<MethodCallInfo> stack = callStack.get();
        if (stack == null) {
            stack = new Stack<>();
            callStack.set(stack);
        }

        // Record start time
        long startTime = System.nanoTime();
        methodStartTime.set(startTime);
        
        // Create method call info
        int depth = stack.size();
        String className = clazz.getSimpleName();

        // System.out.println("ClassName is " + className);
        
        // Clean up ByteBuddy-generated class names
        // if (className.contains("$ByteBuddy$")) {
            // className = className.substring(0, className.indexOf("$ByteBuddy$"));
        // }
        
        // MethodCallInfo callInfo = new MethodCallInfo(
        //         className,
        //         extractMethodName(method),
        //         method,
        //         startTime,
        //         depth
        // );

        MethodCallInfo callInfo = new MethodCallInfo(className, method);
        
        // Push to call stack
        stack.push(callInfo);
        
        // Update call count
        String methodKey = callInfo.getDisplayName();
        // AtomicLong count = methodCallCount.get(methodKey);
        // if (count == null) {
            // count = new AtomicLong(0);
            // methodCallCount.put(methodKey, count);
        // }
        // count.incrementAndGet();
        
        // Get parent method info
        String parentInfo = "";
        if (depth > 0) {
            MethodCallInfo parent = stack.get(depth - 1);
            parentInfo = " ← " + parent.getDisplayName();
        }
        
        // Log method entry with call stack info
        System.out.println("🔵 ENTRY | " + 
                callInfo.getIndentation() + callInfo.getDisplayName() + parentInfo +
                " | Request Object : " + requestStack.get() + 
                " | Flow Object : " + flowStack.get()
                // " | Args: " + formatArguments(arguments) +
                // " | Depth: " + depth
                );
        
        // Show current call stack
        // if (depth > 0) {
            // System.out.println("    📍 Call Stack: " + getCurrentCallStack());
        // }
    }

    /**
     * Advanced method exit advice with call stack tracking
     */
    @Advice.OnMethodExit
    public static void onMethodExit(
            @Advice.Origin String method,
            @Advice.Origin Class<?> clazz,
            @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue) {
        
        // Get call stack
        Stack<MethodCallInfo> stack = callStack.get();
        if (stack == null || stack.isEmpty()) {
            return;
        }
        
        // Pop current method from stack
        MethodCallInfo callInfo = stack.pop();
        
        // Calculate execution time
        // Long startTime = methodStartTime.get();
        // long endTime = System.nanoTime();
        // long duration = startTime != null ? endTime - startTime : 0;
        
        // Update total time
        String methodKey = callInfo.getDisplayName();
        // AtomicLong totalTime = methodTotalTime.get(methodKey);
        // if (totalTime == null) {
            // totalTime = new AtomicLong(0);
            // methodTotalTime.put(methodKey, totalTime);
        // }
        // totalTime.addAndGet(duration);
        
        // Clear thread local if stack is empty
        if (stack.isEmpty()) {
            callStack.remove();
            methodStartTime.remove();
        }
        
        // Format return value
        String returnInfo = "✅ Return: " + formatReturnValue(returnValue);
        
        // Log method exit with call stack info
        System.out.println("🔴 EXIT  | " + 
                Thread.currentThread().getName() + " | " +
                callInfo.getIndentation() + callInfo.getDisplayName() +
                " | " + returnInfo + 
                // " | Duration: " + formatDuration(duration) +
                " | Depth: " + callInfo.depth);
    }

    /**
     * Extract method name from full method signature
     */
    public static String extractMethodName(String fullMethod) {
        if (fullMethod == null) return "unknown";
        
        // Extract method name from signature like "public java.lang.String com.example.Test.method(java.lang.String)"
        int lastDot = fullMethod.lastIndexOf('.');
        if (lastDot == -1) return fullMethod;
        
        String afterLastDot = fullMethod.substring(lastDot + 1);
        int parenIndex = afterLastDot.indexOf('(');
        if (parenIndex == -1) return afterLastDot;
        
        String methodName = afterLastDot.substring(0, parenIndex);
        
        // Clean up ByteBuddy-generated class names
        if (methodName.contains("$ByteBuddy$")) {
            // Extract the original method name from ByteBuddy-generated signature
            String[] parts = methodName.split("\\.");
            if (parts.length > 0) {
                return parts[parts.length - 1];
            }
        }
        
        return methodName;
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
     * Print method statistics with call hierarchy information
     */
    public static void printStatistics() {
        System.out.println("\n📊 METHOD STATISTICS WITH CALL HIERARCHY:");
        System.out.println("=========================================");
        
        for (java.util.Map.Entry<String, AtomicLong> entry : methodCallCount.entrySet()) {
            String method = entry.getKey();
            AtomicLong count = entry.getValue();
            AtomicLong totalTime = methodTotalTime.get(method);
            long avgTime = totalTime != null ? totalTime.get() / count.get() : 0;
            
            System.out.printf("%s | Calls: %d | Avg Time: %s%n", 
                    method, count.get(), formatDuration(avgTime));
        }
    }

    /**
     * Get current call stack depth for a thread
     */
    public static int getCurrentStackDepth() {
        Stack<MethodCallInfo> stack = callStack.get();
        return stack != null ? stack.size() : 0;
    }

    /**
     * Get current call stack as a string
     */
    public static String getCurrentCallStack() {
        Stack<MethodCallInfo> stack = callStack.get();
        if (stack == null || stack.isEmpty()) {
            return "Empty call stack";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stack.size(); i++) {
            MethodCallInfo info = stack.get(i);
            sb.append(info.getIndentation()).append(info.getDisplayName());
            if (i < stack.size() - 1) {
                sb.append(" → ");
            }
        }
        return sb.toString();
    }
}
