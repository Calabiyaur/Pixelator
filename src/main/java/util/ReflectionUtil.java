package main.java.util;

import java.lang.reflect.InvocationTargetException;

import main.java.start.ExceptionHandler;
import org.apache.commons.lang3.reflect.MethodUtils;

public class ReflectionUtil {

    public static Object invokeMethod(final Object object, final String methodName, Object... args) {
        try {
            return MethodUtils.invokeMethod(object, true, methodName, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }
}
