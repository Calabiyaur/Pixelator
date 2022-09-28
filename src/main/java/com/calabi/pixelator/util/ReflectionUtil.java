package com.calabi.pixelator.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.calabi.pixelator.main.ExceptionHandler;

public final class ReflectionUtil {

    public static <T> T instantiate(String className, Param<?>... args) {
        Check.notNull(className);
        try {
            Class<?>[] types = Arrays.stream(args).map(Param::getType).toArray(Class<?>[]::new);
            Object[] objects = Arrays.stream(args).map(Param::getObject).toArray();

            Class<?> type = Class.forName(className);

            Constructor<?> constructor = type.getConstructor(types);
            constructor.setAccessible(true);

            return (T) constructor.newInstance(objects);

        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + className, e);
        }
    }

    public static <T> T invokeMethod(final Object object, final String methodName, Object... args) {
        Check.notNull(object);
        Check.notNull(methodName);
        try {
            return (T) MethodUtils.invokeMethod(object, true, methodName, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }

    public static <T> T getField(final Object object, final String fieldName) {
        Check.notNull(object);
        Check.notNull(fieldName);
        try {
            return (T) FieldUtils.readField(object, fieldName, true);
        } catch (IllegalAccessException e) {
            ExceptionHandler.handle(e);
            return null;
        }
    }

    public static void setField(final Object object, final String fieldName, final Object value) {
        Check.notNull(object);
        Check.notNull(fieldName);
        try {
            FieldUtils.writeField(object, fieldName, value, true);
        } catch (IllegalAccessException e) {
            ExceptionHandler.handle(e);
        }
    }

}
