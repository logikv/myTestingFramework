package myAnnotation;

import myAnnotation.annotation.BeforeSuite;
import myAnnotation.annotation.Test;
import myAnnotation.annotation.TestedClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestSomething {

    private static Class<TestStorage> clazz = TestStorage.class;
    private static Class<? extends java.lang.annotation.Annotation> testClazz = Test.class;
    private static Method[] clazzMethods = clazz.getDeclaredMethods();
    private static Method beforeSuiteMethod;
    private static TestStorage testStorageInstance;

    public static void main(String[] args) {
        if (isTestedClass()) {
            startClassTesting();
        }
    }

    private static void startClassTesting() {
        methodsQueue().forEach(TestSomething::runTestedMethod);
    }


    private static ArrayDeque<Method> methodsQueue() {
        List<Method> methods = getTestedMethods();
        ArrayDeque<Method> queue = new ArrayDeque<>();
        for (int i = 1; i <= 10; i++) {
            int finalI = i;
            methods.stream()
                    .filter(m -> (getMethodPriority(m) == finalI))
                    .forEach(queue::add);
        }
        return queue;
    }

    private static int getMethodPriority(Method method) {
        if (!method.isAnnotationPresent(testClazz)) {
            return 10;
        }
        Test test = (Test) method.getDeclaredAnnotation(testClazz);
        return test.priority();
    }


    private static List<Method> getTestedMethods() {
        Method[] m = clazz.getDeclaredMethods();
        return Arrays.stream(m)
                .filter(method -> method.isAnnotationPresent(testClazz))
                .collect(Collectors.toList());
    }


    private static void newClassInstance() {
        try {
            testStorageInstance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void findBeforeSuite() {
        beforeSuiteMethod = Arrays.stream(clazzMethods)
                .filter(method -> method.isAnnotationPresent(BeforeSuite.class))
                .findAny()
                .orElse(null);
    }

    private static void runTestedMethod(Method method) {
        System.out.print("Start test :: ");
        newClassInstance();
        findBeforeSuite();
        if (beforeSuiteMethod != null) runMethod(beforeSuiteMethod);
        runMethod(method);
    }

    private static void runMethod(Method method) {
        try {
            method.invoke(testStorageInstance);
            System.out.println(method.getName() + " invoked");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static boolean isTestedClass() {
        return clazz.isAnnotationPresent(TestedClass.class);
    }

}