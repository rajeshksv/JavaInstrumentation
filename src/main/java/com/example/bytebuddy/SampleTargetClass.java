package com.example.bytebuddy;

/**
 * Sample target class that will be instrumented by ByteBuddy
 */
public class SampleTargetClass {

    public String simpleMethod(String input) {
        System.out.println("Executing simpleMethod with input: " + input);
        return "Processed: " + input;
    }

    public int calculateSum(int a, int b) {
        System.out.println("Calculating sum of " + a + " and " + b);
        return a + b;
    }

    public void methodWithException() {
        System.out.println("About to throw an exception");
        throw new RuntimeException("This is a test exception");
    }

    public void voidMethod() {
        System.out.println("Executing void method");
    }

    public static String staticMethod(String input) {
        System.out.println("Executing static method with input: " + input);
        return "Static result: " + input.toUpperCase();
    }

    public String methodWithMultipleArgs(String str, int num, boolean flag) {
        System.out.println("Method with multiple arguments: " + str + ", " + num + ", " + flag);
        return "Result: " + str + "_" + num + "_" + flag;
    }
}
