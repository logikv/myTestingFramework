package myAnnotation;


import myAnnotation.annotation.BeforeSuite;
import myAnnotation.annotation.Test;
import myAnnotation.annotation.TestedClass;

@TestedClass
public class TestStorage {

    private static int fileSize;
    private static int storageSize;

    @BeforeSuite
    public static void before(){
        fileSize=5;
        storageSize=30;
    }

    @Test(priority=1)
    public static boolean methodWithVeryHighPriority(){
        fileSize*=2;
        return fileSize >= storageSize;
    }

    @Test(priority=3)
    public static boolean methodWithHighPriority1(){
        fileSize*=3;
        return fileSize >= storageSize;
    }

    @Test(priority=3)
    public static boolean methodWithHighPriority2(){
        fileSize*=4;
        return fileSize >= storageSize;
    }

    @Test(priority=5)
    public static boolean methodWithMiddlePriority(){
        fileSize*=5;
        return fileSize >= storageSize;
    }

    @Test()
    public static boolean methodWithoutPriority(){
        fileSize*=10;
        return fileSize >= storageSize;
    }


}
