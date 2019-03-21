package myAnnotation;

import myAnnotation.annotation.AfterSuite;
import myAnnotation.annotation.BeforeSuite;
import myAnnotation.annotation.Test;
import myAnnotation.annotation.TestedClass;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestSomething {

    private static Class<TestStorage> clazz = TestStorage.class;
    private static Class<? extends java.lang.annotation.Annotation> testClazz = Test.class;
    private static Method[] clazzMethods = clazz.getDeclaredMethods();
    private static Method beforeSuiteMethod;
    private static Method afterSuiteMethod;
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

    private static void findBeforeSuite() throws Exception {
        ArrayList<Method> suits = new ArrayList<>();
        Arrays.stream(clazzMethods)
                .filter(method -> method.isAnnotationPresent(BeforeSuite.class))
                .forEach(suits::add);
        if (suits.size() > 1) {
            throw new Exception("You have more then one BeforeSuite method");
        } else {
            beforeSuiteMethod = suits.get(0);
        }
    }

    private static void findAfterSuite() throws Exception {
        ArrayList<Method> suits = new ArrayList<>();
        Arrays.stream(clazzMethods)
                .filter(method -> method.isAnnotationPresent(AfterSuite.class))
                .forEach(suits::add);
        if (suits.size() > 1) {
            throw new Exception("You have more then one AfterSuite method");
        } else {
            afterSuiteMethod = suits.get(0);
        }
    }

    private static void runTestedMethod(Method method) {
        System.out.println();
        System.out.print("Start test :: ");
        newClassInstance();
        try {
            findBeforeSuite();
            findAfterSuite();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (beforeSuiteMethod != null) runMethod(beforeSuiteMethod);
        runMethod(method);
        System.out.print("Complete test :: ");
        if (afterSuiteMethod != null) runMethod(afterSuiteMethod);
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