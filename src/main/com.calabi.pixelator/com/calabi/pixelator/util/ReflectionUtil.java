package com.calabi.pixelator.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.MethodUtils;

import com.calabi.pixelator.start.ExceptionHandler;

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
