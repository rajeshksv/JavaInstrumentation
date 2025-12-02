package com.example.bytebuddy;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Demo showing ByteBuddy instrumentation with classes loaded in different class loaders
 * This demonstrates how AgentBuilder can instrument classes across different class loaders
 */
public class JavaAgentCustomClassLoaderDemo {

    /**
     * Custom class loader that loads classes from the classpath
     * This class loader will load SampleTargetClass independently
     */
    static class CustomClassLoader extends URLClassLoader {
        private final String classLoaderName;
        
        public CustomClassLoader(String name, URL[] urls, ClassLoader parent) {
            super(urls, parent);
            this.classLoaderName = name;
        }
        
        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            // Only load our target class with this class loader, delegate others to parent
            if (name.equals("com.example.bytebuddy.SampleTargetClass")) {
                synchronized (getClassLoadingLock(name)) {
                    Class<?> c = findLoadedClass(name);
                    if (c == null) {
                        try {
                            // Try to find the class in our URLs first
                            c = findClass(name);
                            if (resolve) {
                                resolveClass(c);
                            }
                            System.out.println("📦 [" + classLoaderName + "] Loaded class: " + name);
                            return c;
                        } catch (ClassNotFoundException e) {
                            // If not found, delegate to parent
                            return super.loadClass(name, resolve);
                        }
                    } else {
                        return c;
                    }
                }
            }
            // For all other classes, delegate to parent
            return super.loadClass(name, resolve);
        }
        
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.equals("com.example.bytebuddy.SampleTargetClass")) {
                String path = name.replace('.', '/') + ".class";
                try (InputStream is = getResourceAsStream(path)) {
                    if (is == null) {
                        throw new ClassNotFoundException(name);
                    }
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(data)) != -1) {
                        buffer.write(data, 0, bytesRead);
                    }
                    byte[] classBytes = buffer.toByteArray();
                    return defineClass(name, classBytes, 0, classBytes.length);
                } catch (Exception e) {
                    throw new ClassNotFoundException(name, e);
                }
            }
            return super.findClass(name);
        }
        
        @Override
        public String toString() {
            return classLoaderName;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== ByteBuddy AgentBuilder Demo with Different Class Loaders ===\n");

        // Install ByteBuddy agent to get Instrumentation instance
        Instrumentation instrumentation = ByteBuddyAgent.install();
        
        // Setup AgentBuilder to instrument SampleTargetClass in ANY class loader
        // This is key - we use ignore() to not filter by class loader
        AgentBuilder agentBuilder = new AgentBuilder.Default()
                .ignore(ElementMatchers.none()) // Don't ignore any class loaders
                .type(ElementMatchers.named("com.example.bytebuddy.SampleTargetClass"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                    System.out.println("🔄 Transforming class: " + typeDescription.getName() + 
                                     " in class loader: " + classLoader);
                    return builder.method(ElementMatchers.any())
                            .intercept(Advice.to(AdvancedMethodInstrumentation.class));
                })
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
        
        // Install the agent
        agentBuilder.installOn(instrumentation);
        
        System.out.println("✓ AgentBuilder installed and ready to instrument classes\n");

        // Test 1: Load class in the default/system class loader
        System.out.println("=== Test 1: Loading class in System Class Loader ===");
        Class<?> systemClass = Class.forName("com.example.bytebuddy.SampleTargetClass");
        Object systemObj = systemClass.getDeclaredConstructor().newInstance();
        SampleTargetClass systemTarget = (SampleTargetClass) systemObj;
        System.out.println("Class loader: " + systemClass.getClassLoader());
        System.out.println("Class: " + systemClass.getName());
        testMethods(systemTarget, "System ClassLoader");
        System.out.println();

        // Test 2: Load class in a custom class loader
        System.out.println("=== Test 2: Loading class in Custom Class Loader #1 ===");
        URL[] urls = {JavaAgentCustomClassLoaderDemo.class.getProtectionDomain()
                .getCodeSource().getLocation()};
        @SuppressWarnings("resource") // Keep class loader alive for demo
        CustomClassLoader customLoader1 = new CustomClassLoader("CustomLoader-1", urls, 
                Thread.currentThread().getContextClassLoader());
        
        Class<?> customClass1 = customLoader1.loadClass("com.example.bytebuddy.SampleTargetClass");
        Object customObj1 = customClass1.getDeclaredConstructor().newInstance();
        // We can't cast directly, so we'll use reflection to call methods
        System.out.println("Class loader: " + customClass1.getClassLoader());
        System.out.println("Class: " + customClass1.getName());
        testMethodsViaReflection(customObj1, customClass1, "Custom ClassLoader #1");
        System.out.println();

        // Test 3: Load class in another custom class loader
        System.out.println("=== Test 3: Loading class in Custom Class Loader #2 ===");
        @SuppressWarnings("resource") // Keep class loader alive for demo
        CustomClassLoader customLoader2 = new CustomClassLoader("CustomLoader-2", urls, 
                Thread.currentThread().getContextClassLoader());
        
        Class<?> customClass2 = customLoader2.loadClass("com.example.bytebuddy.SampleTargetClass");
        Object customObj2 = customClass2.getDeclaredConstructor().newInstance();
        System.out.println("Class loader: " + customClass2.getClassLoader());
        System.out.println("Class: " + customClass2.getName());
        testMethodsViaReflection(customObj2, customClass2, "Custom ClassLoader #2");
        System.out.println();

        // Print final statistics
        System.out.println("=== Final Statistics ===");
        AdvancedMethodInstrumentation.printStatistics();

        System.out.println("\n=== Demo Complete ===");
        System.out.println("Note: Classes loaded in different class loaders are separate instances,");
        System.out.println("but AgentBuilder successfully instruments all of them!");
    }

    /**
     * Test methods on a SampleTargetClass instance (when we can cast directly)
     */
    private static void testMethods(SampleTargetClass target, String loaderName) {
        System.out.println("--- Testing methods in " + loaderName + " ---");
        target.simpleMethod("test from " + loaderName);
        target.calculateSum(10, 20);
        target.voidMethod();
    }

    /**
     * Test methods via reflection (when classes are in different class loaders)
     */
    private static void testMethodsViaReflection(Object target, Class<?> targetClass, String loaderName) {
        System.out.println("--- Testing methods in " + loaderName + " (via reflection) ---");
        try {
            // Call simpleMethod
            targetClass.getMethod("simpleMethod", String.class)
                    .invoke(target, "test from " + loaderName);
            
            // Call calculateSum
            targetClass.getMethod("calculateSum", int.class, int.class)
                    .invoke(target, 10, 20);
            
            // Call voidMethod
            targetClass.getMethod("voidMethod").invoke(target);
        } catch (Exception e) {
            System.err.println("Error calling methods: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

